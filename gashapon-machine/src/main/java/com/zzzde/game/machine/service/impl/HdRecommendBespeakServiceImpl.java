package com.zzzde.game.machine.service.impl;

import cn.net.yugu.doraemon.api.common.response.DataResult;
import cn.net.yugu.metis.api.model.BatteryInfo;
import cn.net.yugu.metis.api.model.HdRecommendBespeakQueryRequest;
import cn.net.yugu.metis.api.model.HdRecommendBespeakQueryResponse;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zzzde.game.machine.domain.*;
import com.zzzde.game.machine.service.HdRecommendBespeakService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author zzzde
 * @Date 2023/11/13
 */
@Slf4j
@Service
public class HdRecommendBespeakServiceImpl implements HdRecommendBespeakService {

    /* 伪预约个性化推荐换电规则相关配置 */
    /**
     * 每组被标记为预约电池的数量阈值
     */
    @Value("${metis.bespeak.battery.group.max.num:90}")
    private int bespeakBatteryGroupSplitBatteryVolume;
    /**
     * 每组被标记为预约电池的数量阈值
     */
    @Value("${metis.bespeak.battery.group.max.num:2}")
    private int bespeakBatteryGroupMaxNum;

    /**
     * 每组高电量候选集阈值
     */
    @Value("${metis.bespeak.high.volume.battery.split.num:3}")
    private int bespeakHighVolumeBatterySplitNum;

    /**
     * 伪预约标记电池最小容量
     */
    @Value("${metis.bespeak.battery.min.capacity:18000}")
    private int bespeakBatteryMinCapacity;

    // 电量降序
    private static final Comparator<? super CabinetBoxInfoDTO> VOLUME_DESC_COMPARATOR = Comparator.comparingInt(CabinetBoxInfoDTO::getBatteryVolume).reversed();
    private static final Comparator<? super CabinetBoxInfoDTO> DEFAULT_DESC_COMPARATOR = Comparator.comparing(CabinetBoxInfoDTO::getBatteryId).reversed();

    // 电量降序
    private static final Comparator<? super BatteryInfoDTO> VOLUME_DESC_COMPARATOR_BATTERY = Comparator.comparingInt(BatteryInfoDTO::getVolume).reversed();
    private static final Comparator<? super BatteryInfoDTO> DEFAULT_DESC_COMPARATOR_BATTERY = Comparator.comparing(BatteryInfoDTO::getBatteryId).reversed();

    @Override
    public DataResult<HdRecommendBespeakQueryResponse> recommendBatteriesBespeakQuery(HdRecommendBespeakQueryRequest recommendationRequest) {

        HdMarkBespeakBatteryReqDTO hdMarkBespeakBatteryReqDTO = HdMarkBespeakBatteryReqDTO.builder().build();
        List<CabinetBoxInfoDTO> batteryInfos = recommendationRequest.getCabinetBoxList().stream()
                .map(x-> CabinetBoxInfoDTO.builder()
                        .boxNum(x.getBoxNum())
                        .cabinetId(x.getCabinetId())
                        .isActive(x.getIsActive())
                        .boxStatus(x.getBoxStatus())
                        .isOpen(x.getIsOpen())
                        .lockType(x.getLockType())
                        .lockUserId(x.getLockUserId())
                        .batteryId(x.getBatteryId())
                        .batteryVolume(x.getBatteryVolume())
                        .batteryVoltage(x.getBatteryVoltage())
                        .batteryCapacity(x.getBatteryCapacity())
                        .build())
                .collect(Collectors.toList());
        hdMarkBespeakBatteryReqDTO.setSystemSource(recommendationRequest.getSystemSource());
        hdMarkBespeakBatteryReqDTO.setCabinetId(recommendationRequest.getCabinetId());
        hdMarkBespeakBatteryReqDTO.setObserver(recommendationRequest.getObserver());
        hdMarkBespeakBatteryReqDTO.setQueryTimestamp(recommendationRequest.getQueryTimestamp());
        hdMarkBespeakBatteryReqDTO.setCabinetBoxInfoDTOList(batteryInfos);

        // 电池信息重组方便赋值
        Map<String, HdRecommendBespeakQueryRequest.Battery> batteryMap = recommendationRequest.getBatteryList().stream().collect(Collectors.toMap(HdRecommendBespeakQueryRequest.Battery::getBatteryId, battery -> battery));
        // 电池信息赋值
        batteryInfos.forEach(box->{
            if (StringUtils.isNotEmpty(box.getBatteryId())){
                HdRecommendBespeakQueryRequest.Battery boxBattery = batteryMap.get(box.getBatteryId());
                box.setStatus(boxBattery.getStatus());
                box.setBatteryType(boxBattery.getBatteryType());
                box.setBatterySku(boxBattery.getBatterySku());
                box.setCustomerId(boxBattery.getCustomerId());
                box.setOrderId(boxBattery.getOrderId());
                box.setBespeakCustomerId(boxBattery.getBespeakCustomerId());
                box.setBespeakOrderId(boxBattery.getBespeakOrderId());
                box.setBespeakType(boxBattery.getBespeakType());
            }
        });

        HdMarkBespeakBatteryRspDTO markBespeakBatteryRspDTO = markBespeakBattery(hdMarkBespeakBatteryReqDTO);
        HdRecommendBespeakQueryResponse response = new HdRecommendBespeakQueryResponse();
        List<BatteryInfoDTO> batteryList = markBespeakBatteryRspDTO.getBatteryList();
        List<BatteryInfo> batteryInfo = Optional.ofNullable(batteryList).map(x-> x.stream().map(xx->{
            BatteryInfo tempBatteryInfo = new BatteryInfo();
            tempBatteryInfo.setBatteryId(xx.getBatteryId());
            tempBatteryInfo.setBoxNum(xx.getBoxNum());
            tempBatteryInfo.setVolume(xx.getVolume());
            tempBatteryInfo.setCapacity(xx.getCapacity());
            return tempBatteryInfo;
        }).collect(Collectors.toList())).orElse(null);
        response.setBatteryList(batteryInfo);
        return DataResult.successWithData(response);
    }

    public HdMarkBespeakBatteryRspDTO markBespeakBattery(HdMarkBespeakBatteryReqDTO hdMarkBespeakBatteryReqDTO) {
        List<CabinetBoxInfoDTO> cabinetBoxInfoDTOList = hdMarkBespeakBatteryReqDTO.getCabinetBoxInfoDTOList();
        // 电池类型分组
        Map<Integer, List<CabinetBoxInfoDTO>> batteryGroupMap = cabinetBoxInfoDTOList.stream()
                .filter(x -> StringUtils.isNotEmpty(x.getBatteryId())) // 过滤空格口
                .collect(Collectors.groupingBy(CabinetBoxInfoDTO::getBatteryType)); // 分组
        // 电池类型+sku类型分组
        //        Map<Integer, Map<Integer, List<HdRecommendBespeakQueryRequest.Battery>>> batteryGroupMap = batteryList.stream()
        //          .collect(Collectors.groupingBy(HdRecommendBespeakQueryRequest.Battery::getBatteryType, Collectors.groupingBy(HdRecommendBespeakQueryRequest.Battery::getBatterySku)));

        List<BatteryInfoDTO> fakeBatteryList = new ArrayList<>();
        Optional.ofNullable(batteryGroupMap).orElse(new HashMap<>()).forEach((batteryType, tempGroupBattery) -> {
            // 已标记电池循环标记
            int markFlagNum = 0;
            List<CabinetBoxInfoDTO> candidateCollect;
            // 满足分割电量的候选集合
            candidateCollect = tempGroupBattery.stream()
                    .filter(x -> x.getBatteryVolume() >= bespeakBatteryGroupSplitBatteryVolume)
                    .sorted(VOLUME_DESC_COMPARATOR).sorted(DEFAULT_DESC_COMPARATOR)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(candidateCollect)) {
                // 计算候选集中最高电池容量
                int groupBatteryMaxCapacity = candidateCollect.stream().max(Comparator.comparing(CabinetBoxInfoDTO::getBatteryCapacity)).get().getBatteryCapacity();
                // 按从高到低顺序，判断电池容量情况
                for (int i = 0; i <= candidateCollect.size() - bespeakHighVolumeBatterySplitNum; i++) {
                    CabinetBoxInfoDTO dto = candidateCollect.get(i);
                    if (dto.getBatteryVolume() < groupBatteryMaxCapacity
                            && dto.getBatteryCapacity() < bespeakBatteryMinCapacity
                            && markFlagNum < bespeakBatteryGroupMaxNum) {
                        fakeBatteryList.add(BatteryInfoDTO.builder()
                                .batteryId(dto.getBatteryId())
                                .boxNum(dto.getBoxNum())
                                .capacity(dto.getBatteryCapacity())
                                .volume(dto.getBatteryVolume()).build());
                        markFlagNum++;
                    }
                }
            }
        });
        HdMarkBespeakBatteryRspDTO responseDTO = new HdMarkBespeakBatteryRspDTO();
        responseDTO.setBatteryList(fakeBatteryList);
        return responseDTO;
    }

    /**
     * @param hdExchangeBespeakBatteryReqDTO 待计算电池集合
     * @return
     */
    @Override
    public HdMarkBespeakBatteryRspDTO bespeakBatteryExchange(HdExchangeBespeakBatteryReqDTO hdExchangeBespeakBatteryReqDTO) {
        List<BatteryInfoDTO> batteryList = hdExchangeBespeakBatteryReqDTO.getBatteryInfoList();
        // 可用电池类型分组
        Map<Integer, List<BatteryInfoDTO>> batteryGroupMap = Optional.ofNullable(batteryList).orElse(new ArrayList<>()).stream()
                .filter(x -> StringUtils.isNotEmpty(x.getBatteryId())) // 过滤空格口
                .collect(Collectors.groupingBy(BatteryInfoDTO::getBatteryType)); // 分组

        List<BatteryInfoDTO> fakeBatteryList = new ArrayList<>();

        Optional.ofNullable(batteryGroupMap).orElse(new HashMap<>()).forEach((batteryType, tempGroupBattery) -> {
            // 已标记电池循环标记
            int markFlagNum = 0;
            List<BatteryInfoDTO> candidateCollect;
            // 满足分割电量的候选集合
            candidateCollect = tempGroupBattery.stream()
                    .filter(x -> x.getVolume() >= bespeakBatteryGroupSplitBatteryVolume)
                    .sorted(VOLUME_DESC_COMPARATOR_BATTERY).sorted(DEFAULT_DESC_COMPARATOR_BATTERY)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(candidateCollect)) {
                // 计算候选集中最高电池容量
                int groupBatteryMaxCapacity = candidateCollect.stream().max(Comparator.comparing(BatteryInfoDTO::getCapacity)).get().getCapacity();
                // 按从高到低顺序，判断电池容量情况
                for (int i = 0; i <= candidateCollect.size() - bespeakHighVolumeBatterySplitNum; i++) {
                    BatteryInfoDTO dto = candidateCollect.get(i);
                    if (dto.getVolume() < groupBatteryMaxCapacity
                            && dto.getCapacity() < bespeakBatteryMinCapacity
                            && markFlagNum < bespeakBatteryGroupMaxNum) {
                        fakeBatteryList.add(BatteryInfoDTO.builder()
                                .batteryId(dto.getBatteryId())
                                .boxNum(dto.getBoxNum())
                                .capacity(dto.getCapacity())
                                .volume(dto.getVolume()).build());
                        markFlagNum++;
                    }
                }
            }
        });
        HdMarkBespeakBatteryRspDTO responseDTO = new HdMarkBespeakBatteryRspDTO();
        responseDTO.setBatteryList(fakeBatteryList);
        return responseDTO;
    }
}
