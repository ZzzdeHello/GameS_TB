package com.zzzde.game.machine.service.impl;

import cn.net.yugu.doraemon.api.common.response.DataResult;
import cn.net.yugu.metis.api.model.BatteryInfo;
import cn.net.yugu.metis.api.model.BatteryQueryInfo;
import cn.net.yugu.metis.api.model.HdRecommendBespeakQueryRequest;
import cn.net.yugu.metis.api.model.HdRecommendBespeakQueryResponse;
import com.alibaba.fastjson.JSON;
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
    @Value("${metis.bespeak.high.volume.battery.split.num:2}")
    private int bespeakHighVolumeBatterySplitNum;

    /**
     * 伪预约标记电池最小容量
     */
    @Value("${metis.bespeak.battery.min.capacity:25000}")
    private int bespeakBatterySplitCapacity;

    // 电量降序并容量升序
    private static final Comparator<? super BatteryInfoDTO> BATTERY_VOLUME_DESC_AND_CAPACITY_ASC_COMPARATOR = Comparator.comparing(BatteryInfoDTO::getVolume,Comparator.reverseOrder()).thenComparing(BatteryInfoDTO::getCapacity).thenComparing(BatteryInfoDTO::getBatteryId);
    // 电量降序并容量降序
    private static final Comparator<? super BatteryInfoDTO> BATTERY_VOLUME_DESC_AND_CAPACITY_DESC_COMPARATOR = Comparator.comparing(BatteryInfoDTO::getVolume,Comparator.reverseOrder()).thenComparing(BatteryInfoDTO::getCapacity, Comparator.reverseOrder()).thenComparing(BatteryInfoDTO::getBatteryId);
    // 容量降序
    private static final Comparator<? super BatteryInfoDTO> BATTERY_CAPACITY_DESC_COMPARATOR = Comparator.comparingInt(BatteryInfoDTO::getCapacity).thenComparing(BatteryInfoDTO::getBatteryId).reversed();
    // 容量升序
    private static final Comparator<? super BatteryInfoDTO> BATTERY_CAPACITY_ASC_COMPARATOR = Comparator.comparingInt(BatteryInfoDTO::getCapacity).thenComparing(BatteryInfoDTO::getBatteryId);


    @Override
    public DataResult<HdRecommendBespeakQueryResponse> recommendBatteriesBespeakQuery(HdRecommendBespeakQueryRequest recommendationRequest, Integer type) {

        HdMarkBespeakBatteryReqDTO hdMarkBespeakBatteryReqDTO = HdMarkBespeakBatteryReqDTO.builder().build();
        List<CabinetBoxInfoDTO> batteryInfos = recommendationRequest.getCabinetBoxList().stream()
                .map(x -> {
                    CabinetBoxInfoDTO d = new CabinetBoxInfoDTO();
                    d.setBoxNum(x.getBoxNum());
                    d.setCabinetId(x.getCabinetId());
                    d.setIsActive(x.getIsActive());
                    d.setBoxStatus(x.getBoxStatus());
                    d.setIsOpen(x.getIsOpen());
                    d.setLockType(x.getLockType());
                    d.setLockUserId(x.getLockUserId());
                    d.setBatteryId(x.getBatteryId());
                    d.setVolume(x.getBatteryVolume());
                    d.setCapacity(x.getBatteryCapacity());
                    return d;
                }).collect(Collectors.toList());
        hdMarkBespeakBatteryReqDTO.setSystemSource(recommendationRequest.getSystemSource());
        hdMarkBespeakBatteryReqDTO.setCabinetId(recommendationRequest.getCabinetId());
        hdMarkBespeakBatteryReqDTO.setObserver(recommendationRequest.getObserver());
        hdMarkBespeakBatteryReqDTO.setQueryTimestamp(recommendationRequest.getQueryTimestamp());
        hdMarkBespeakBatteryReqDTO.setCabinetBoxInfoDTOList(batteryInfos);

        // 电池信息重组方便赋值
        Map<String, BatteryQueryInfo> batteryMap = Optional.ofNullable(recommendationRequest.getBatteryList()).orElse(new ArrayList<>()).stream().collect(Collectors.toMap(BatteryQueryInfo::getBatteryId, battery -> battery));
        // 电池信息赋值
        Optional.ofNullable(batteryInfos).orElse(new ArrayList<>()).forEach(box -> {
            if (StringUtils.isNotEmpty(box.getBatteryId()) && batteryMap.containsKey(box.getBatteryId())) {
                BatteryQueryInfo boxBattery = batteryMap.get(box.getBatteryId());
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

        HdMarkBespeakBatteryRspDTO markBespeakBatteryRspDTO;
        if (type.equals(1)) {
            markBespeakBatteryRspDTO = markBespeakBatteryB(hdMarkBespeakBatteryReqDTO);
        } else {
            markBespeakBatteryRspDTO = markBespeakBatteryC(hdMarkBespeakBatteryReqDTO);
        }
        HdRecommendBespeakQueryResponse response = new HdRecommendBespeakQueryResponse();
        List<BatteryInfoDTO> batteryList = markBespeakBatteryRspDTO.getBatteryList();
        List<BatteryInfo> batteryInfo = Optional.ofNullable(batteryList).map(x -> x.stream().map(xx -> {
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


    public HdMarkBespeakBatteryRspDTO markBespeakBatteryC(HdMarkBespeakBatteryReqDTO hdMarkBespeakBatteryReqDTO) {
        List<CabinetBoxInfoDTO> cabinetBoxInfoDTOList = hdMarkBespeakBatteryReqDTO.getCabinetBoxInfoDTOList();
        // 电池类型分组
        Map<Integer, List<CabinetBoxInfoDTO>> batteryGroupMap = cabinetBoxInfoDTOList.stream()
                .filter(x -> StringUtils.isNotEmpty(x.getBatteryId())) // 过滤空格口
                .filter(x -> x.getBatteryType() != null) // 过滤电池类型为空的数据
                .filter(x -> x.getBespeakCustomerId() == null) // 过滤预约电池类型的数据
                .filter(x -> x.getIsOpen() != null && x.getIsOpen() == 0)
                .filter(x -> x.getIsActive() != null && x.getIsActive() == 1)
                .filter(x -> x.getBoxStatus() != null && x.getBoxStatus() == 2 )// 保留满箱的柜子
                .filter(x -> x.getStatus() != null && x.getStatus() == 1) // 保留未使用电池
                .collect(Collectors.groupingBy(CabinetBoxInfoDTO::getBatteryType)); // 分组
        // 电池类型+sku类型分组
        //        Map<Integer, Map<Integer, List<HdRecommendBespeakQueryRequest.Battery>>> batteryGroupMap = batteryList.stream()
        //          .collect(Collectors.groupingBy(HdRecommendBespeakQueryRequest.Battery::getBatteryType, Collectors.groupingBy(HdRecommendBespeakQueryRequest.Battery::getBatterySku)));

        List<BatteryInfoDTO> fakeBatteryList = new ArrayList<>();
        Optional.ofNullable(batteryGroupMap).orElse(new HashMap<>()).forEach((batteryType, tempGroupBattery) -> {
            List<BatteryInfoDTO> tempGroup = tempGroupBattery.stream().map(x ->
                    BatteryInfoDTO.builder()
                            .batteryId(x.getBatteryId())
                            .capacity(x.getCapacity())
                            .volume(x.getVolume())
                            .boxNum(x.getBoxNum())
                            .batteryType(x.getBatteryType())
                            .batterySku(x.getBatterySku())
                            .extra(x.getExtra())
                            .build()
            ).collect(Collectors.toList());
            // 规则计算
            fakeBatteryMarkMethodForC(fakeBatteryList, tempGroup);
        });
        if (fakeBatteryList.size() > 0) {
            Set<String> fakeBatterySet = fakeBatteryList.stream().map(BatteryInfoDTO::getBatteryId).collect(Collectors.toSet());
            cabinetBoxInfoDTOList.forEach(z -> {
                if (fakeBatterySet.contains(z.getBatteryId())) {
                    z.setFakeBespeak(1);
                } else {
                    z.setFakeBespeak(0);
                }
            });
            hdMarkBespeakBatteryReqDTO.setCabinetBoxInfoDTOList(cabinetBoxInfoDTOList);
            System.out.println("========发送消息");
        }

        HdMarkBespeakBatteryRspDTO responseDTO = new HdMarkBespeakBatteryRspDTO();
        responseDTO.setBatteryList(fakeBatteryList);
        return responseDTO;
    }

    public HdMarkBespeakBatteryRspDTO markBespeakBatteryB(HdMarkBespeakBatteryReqDTO hdMarkBespeakBatteryReqDTO) {
        List<CabinetBoxInfoDTO> cabinetBoxInfoDTOList = hdMarkBespeakBatteryReqDTO.getCabinetBoxInfoDTOList();
        // 电池类型分组
        Map<Integer, List<CabinetBoxInfoDTO>> batteryGroupMap = cabinetBoxInfoDTOList.stream()
                .filter(x -> StringUtils.isNotEmpty(x.getBatteryId())) // 过滤空格口
                .filter(x -> x.getBatteryType() != null) // 过滤电池类型为空的数据
                .filter(x -> x.getBespeakCustomerId() == null) // 过滤预约电池类型的数据
                .filter(x -> x.getIsOpen() != null && x.getIsOpen() == 0)
                .filter(x -> x.getIsActive() != null && x.getIsActive() == 1)
                .filter(x -> x.getBoxStatus() != null && x.getBoxStatus() == 2 )// 保留满箱的柜子
                .filter(x -> x.getStatus() != null && x.getStatus() == 1) // 保留未使用电池
                .collect(Collectors.groupingBy(CabinetBoxInfoDTO::getBatteryType)); // 分组
        // 电池类型+sku类型分组
        //        Map<Integer, Map<Integer, List<HdRecommendBespeakQueryRequest.Battery>>> batteryGroupMap = batteryList.stream()
        //          .collect(Collectors.groupingBy(HdRecommendBespeakQueryRequest.Battery::getBatteryType, Collectors.groupingBy(HdRecommendBespeakQueryRequest.Battery::getBatterySku)));

        List<BatteryInfoDTO> fakeBatteryList = new ArrayList<>();
        Optional.ofNullable(batteryGroupMap).orElse(new HashMap<>()).forEach((batteryType, tempGroupBattery) -> {
            List<BatteryInfoDTO> collect = tempGroupBattery.stream().map(x ->
                    BatteryInfoDTO.builder()
                            .batteryId(x.getBatteryId())
                            .capacity(x.getCapacity())
                            .volume(x.getVolume())
                            .boxNum(x.getBoxNum())
                            .batteryType(x.getBatteryType())
                            .batterySku(x.getBatterySku())
                            .extra(x.getExtra())
                            .build()
            ).collect(Collectors.toList());
            // 规则计算
            fakeBatteryMarkMethodForB(fakeBatteryList, collect);
            System.out.println(fakeBatteryList);
        });
        if (fakeBatteryList.size() > 0) {
            Set<String> fakeBatterySet = fakeBatteryList.stream().map(BatteryInfoDTO::getBatteryId).collect(Collectors.toSet());
            cabinetBoxInfoDTOList.forEach(z -> {
                if (fakeBatterySet.contains(z.getBatteryId())) {
                    z.setFakeBespeak(1);
                } else {
                    z.setFakeBespeak(0);
                }
            });
            hdMarkBespeakBatteryReqDTO.setCabinetBoxInfoDTOList(cabinetBoxInfoDTOList);
            System.out.println("===============消息发送===");
        }

        HdMarkBespeakBatteryRspDTO responseDTO = new HdMarkBespeakBatteryRspDTO();
        responseDTO.setBatteryList(fakeBatteryList);
        return responseDTO;
    }


    /**
     * @param hdExchangeBespeakBatteryReqDTO 待计算电池集合
     * @return
     */
    @Override
    public HdMarkBespeakBatteryRspDTO bespeakBatteryExchangeB(HdExchangeBespeakBatteryReqDTO hdExchangeBespeakBatteryReqDTO) {
        List<BatteryInfoDTO> batteryList = hdExchangeBespeakBatteryReqDTO.getBatteryInfoList();
        // 可用电池类型分组
        Map<Integer, List<BatteryInfoDTO>> batteryGroupMap = batteryList.stream()
                .filter(x -> StringUtils.isNotEmpty(x.getBatteryId())) // 过滤空格口
                .filter(x -> x.getBatteryType() != null)
                .collect(Collectors.groupingBy(BatteryInfoDTO::getBatteryType)); // 分组

        List<BatteryInfoDTO> fakeBatteryList = new ArrayList<>();

        Optional.ofNullable(batteryGroupMap).orElse(new HashMap<>()).forEach((batteryType, tempGroupBattery) -> {
            // 已标记电池循环标记
            fakeBatteryMarkMethodForB(fakeBatteryList,tempGroupBattery);
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
    public HdMarkBespeakBatteryRspDTO bespeakBatteryExchangeC(HdExchangeBespeakBatteryReqDTO hdExchangeBespeakBatteryReqDTO) {
        List<BatteryInfoDTO> batteryList = hdExchangeBespeakBatteryReqDTO.getBatteryInfoList();
        // 可用电池类型分组
        Map<Integer, List<BatteryInfoDTO>> batteryGroupMap = batteryList.stream()
                .filter(x -> StringUtils.isNotEmpty(x.getBatteryId())) // 过滤空格口
                .filter(x -> x.getBatteryType() != null)
                .collect(Collectors.groupingBy(BatteryInfoDTO::getBatteryType)); // 分组

        List<BatteryInfoDTO> fakeBatteryList = new ArrayList<>();

        Optional.ofNullable(batteryGroupMap).orElse(new HashMap<>()).forEach((batteryType, tempGroupBattery) -> {
            // 已标记电池循环标记
            fakeBatteryMarkMethodForC(fakeBatteryList,tempGroupBattery);
        });
        HdMarkBespeakBatteryRspDTO responseDTO = new HdMarkBespeakBatteryRspDTO();
        responseDTO.setBatteryList(fakeBatteryList);
        return responseDTO;
    }

    private void fakeBatteryMarkMethodForB(List<BatteryInfoDTO> fakeBatteryList, List<BatteryInfoDTO> tempGroupBattery) {
        // 已标记电池循环标记
        int markFlagNum = 0;
        List<BatteryInfoDTO> candidateCollect;
        List<BatteryInfoDTO> candidateVolumeCollect;
        // 满足分割电量的候选集合,并按电池容量排序
        candidateCollect = tempGroupBattery.stream()
                .filter(x -> StringUtils.isNotEmpty(x.getBatteryId()))
                .filter(x -> x.getCapacity() != null)
                .filter(x -> x.getVolume() != null)
                .filter(x -> x.getVolume() >= bespeakBatteryGroupSplitBatteryVolume)
                .sorted(BATTERY_CAPACITY_ASC_COMPARATOR)
                .collect(Collectors.toList());
        System.out.println("candidateCollect" + JSON.toJSONString(candidateCollect));
        // 满足分割电量的候选集合,并按电池电量排序
        candidateVolumeCollect = tempGroupBattery.stream()
                .filter(x -> StringUtils.isNotEmpty(x.getBatteryId()))
                .filter(x -> x.getCapacity() != null)
                .filter(x -> x.getVolume() != null)
                .filter(x -> x.getVolume() >= bespeakBatteryGroupSplitBatteryVolume)
                .sorted(BATTERY_VOLUME_DESC_AND_CAPACITY_ASC_COMPARATOR)
                .collect(Collectors.toList());
        System.out.println("candidateVolumeCollect" + JSON.toJSONString(candidateCollect));
        System.out.println("candidateVolumeCollect" + JSON.toJSONString(candidateVolumeCollect));
        if (CollectionUtils.isNotEmpty(candidateCollect)) {
            for (int i = 0; i <= candidateCollect.size() - bespeakHighVolumeBatterySplitNum ; i++) {
                BatteryInfoDTO dto = candidateCollect.get(i);
                if (dto.getCapacity() <= bespeakBatterySplitCapacity) {
                    String batteryId = candidateVolumeCollect.get(i).getBatteryId();
                    if (batteryId.equals(dto.getBatteryId()) && markFlagNum < bespeakBatteryGroupMaxNum) {
                        fakeBatteryList.add(BatteryInfoDTO.builder()
                                .batteryId(dto.getBatteryId())
                                .boxNum(dto.getBoxNum())
                                .capacity(dto.getCapacity())
                                .volume(dto.getVolume()).build());
                        markFlagNum++;
                    } else {
                        break;
                    }
                }
            }
        }
        System.out.println("method-fakeBatteryList" + JSON.toJSONString(fakeBatteryList));
    }

    private void fakeBatteryMarkMethodForC(List<BatteryInfoDTO> fakeBatteryList, List<BatteryInfoDTO> tempGroupBattery) {
        // 已标记电池循环标记
        int markFlagNum = 0;
        List<BatteryInfoDTO> candidateCollect;
        List<BatteryInfoDTO> candidateVolumeCollect;
        // 满足分割电量的候选集合,并按电池容量排序
        candidateCollect = tempGroupBattery.stream()
                .filter(x -> StringUtils.isNotEmpty(x.getBatteryId()))
                .filter(x -> x.getCapacity() != null)
                .filter(x -> x.getVolume() != null)
                .filter(x -> x.getVolume() >= bespeakBatteryGroupSplitBatteryVolume)
                .sorted(BATTERY_CAPACITY_DESC_COMPARATOR)
                .collect(Collectors.toList());
        // 满足分割电量的候选集合,并按电池电量排序
        candidateVolumeCollect = tempGroupBattery.stream()
                .filter(x -> StringUtils.isNotEmpty(x.getBatteryId()))
                .filter(x -> x.getCapacity() != null)
                .filter(x -> x.getVolume() != null)
                .filter(x -> x.getVolume() >= bespeakBatteryGroupSplitBatteryVolume)
                .sorted(BATTERY_VOLUME_DESC_AND_CAPACITY_DESC_COMPARATOR)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(candidateCollect)) {
            for (int i = 0; i <= candidateCollect.size() - bespeakHighVolumeBatterySplitNum; i++) {
                BatteryInfoDTO dto = candidateCollect.get(i);
                if (dto.getCapacity() >= bespeakBatterySplitCapacity) {
                    String batteryId = candidateVolumeCollect.get(i).getBatteryId();
                    if (batteryId.equals(dto.getBatteryId()) && markFlagNum < bespeakBatteryGroupMaxNum) {
                        fakeBatteryList.add(BatteryInfoDTO.builder()
                                .batteryId(dto.getBatteryId())
                                .boxNum(dto.getBoxNum())
                                .capacity(dto.getCapacity())
                                .volume(dto.getVolume()).build());
                        markFlagNum++;
                    } else {
                        break;
                    }
                }
            }
        }
    }
}
