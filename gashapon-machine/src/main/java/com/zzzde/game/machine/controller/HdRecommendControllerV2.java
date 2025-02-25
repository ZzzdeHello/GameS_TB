package com.zzzde.game.machine.controller;

import cn.net.yugu.doraemon.api.common.response.DataResult;
import cn.net.yugu.metis.api.model.*;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zzzde.game.machine.domain.CabinetBatteryListResponse;
import com.zzzde.game.machine.domain.HdExchangeBespeakBatteryReqDTO;
import com.zzzde.game.machine.domain.HdMarkBespeakBatteryRspDTO;
import com.zzzde.game.machine.service.HdRecommendBespeakService;
import com.zzzde.game.tb.common.dal.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author zzzde
 * @Date 2023/10/10
 */
@Controller
@RequestMapping("/machine/hd")
public class HdRecommendControllerV2 {

    @Value("${metis.bespeak.cabinet.end.rule:1,4,5}")
    private String bespeakCabinetEndRule;

    @Autowired
    HdRecommendBespeakService hdRecommendBespeakService;

    @PostMapping("recommend/v2")
    @ResponseBody
    public ResponseDTO<CabinetBatteryListResponse> hdRecommend(@RequestBody CabinetBatteryListResponse response) {
        ResponseDTO<CabinetBatteryListResponse> responseDTO = new ResponseDTO<>();
        if (!enableFilterCabinetByConfig(response.cabinetId,bespeakCabinetEndRule)){
            responseDTO.setCode(1);
            responseDTO.setMessage("成功");
            return responseDTO;
        }
        List<BatteryInfo> fakeBatteryList = new ArrayList<>();

        HdRecommendBespeakQueryRequest hdRecommendBespeakQueryRequest = new HdRecommendBespeakQueryRequest();
        hdRecommendBespeakQueryRequest.setObserver(1L);
        hdRecommendBespeakQueryRequest.setCabinetId("gz001");
        hdRecommendBespeakQueryRequest.setSystemSource(1);
        hdRecommendBespeakQueryRequest.setQueryTimestamp(System.currentTimeMillis());
        hdRecommendBespeakQueryRequest.setBatteryList(response.batteryList.stream().map(x->{
            BatteryQueryInfo battery = new BatteryQueryInfo();
            battery.setBatteryId(x.batteryId);
            battery.setBatteryType(x.batteryType);
            battery.setStatus(x.status);
            battery.setBoxNum(x.boxNum);
            battery.setCustomerId(x.customerId);
            battery.setOrderId(x.orderId);
            battery.setBespeakCustomerId(x.bespeakCustomerId);
            battery.setBespeakOrderId(x.bespeakOrderId);
            battery.setBespeakType(x.bespeakType);
            return battery;
        }).collect(Collectors.toList()));
        hdRecommendBespeakQueryRequest.setCabinetBoxList(response.cabinetBoxList.stream().map(y->{
            CabinetBoxQueryInfo cabinetBox = new CabinetBoxQueryInfo();
            cabinetBox.setBatteryId(y.batteryId);
            cabinetBox.setBatteryVolume(y.batteryVolume);
            cabinetBox.setBatteryCapacity(y.batteryCapacity);
            cabinetBox.setBatteryVoltage(y.batteryVoltage);
            cabinetBox.setBoxNum(y.boxNum);
            cabinetBox.setBoxStatus(y.boxStatus);
            cabinetBox.setIsActive(y.isActive);
            cabinetBox.setLockUserId(y.lockUserId);
            cabinetBox.setLockType(y.lockType);
            cabinetBox.setIsOpen(y.isOpen);
            return cabinetBox;
        }).collect(Collectors.toList()));
        DataResult<HdRecommendBespeakQueryResponse> queryResponseDataResult = hdRecommendBespeakService.recommendBatteriesBespeakQuery(hdRecommendBespeakQueryRequest,response.type);
        if (queryResponseDataResult.isSuccess() && queryResponseDataResult.getData() != null && !queryResponseDataResult.getData().getBatteryList().isEmpty()){
            fakeBatteryList = queryResponseDataResult.getData().getBatteryList();
        }
        Set<String> fakeBatterySet = fakeBatteryList.stream().map(BatteryInfo::getBatteryId).collect(Collectors.toSet());
        response.batteryList.forEach(z->{
            if (fakeBatterySet.contains(z.batteryId)){
                z.fakeBespeak = 1;
            }else{
                z.fakeBespeak = 0;
            }
        });

        System.out.println("--------------------------------------------------" + JSON.toJSONString(response));
        responseDTO.setData(response);
        responseDTO.setCode(1);
        responseDTO.setMessage("成功");
        return responseDTO;
    }

    /**
     * 是否根据规则过滤柜子
     *
     * @param cabinetId 柜子Id
     * @param dynamicCabinetConfig nacos柜子配置
     * @return true 标识可通过 ;false标识不满足规则直接返回
     */
    private boolean enableFilterCabinetByConfig(String cabinetId, String dynamicCabinetConfig) {
        // 是否进入规则计算
        boolean b = false;
        if (StringUtils.isNotBlank(dynamicCabinetConfig)) {
            String[] split = dynamicCabinetConfig.split(",");
            for (String end : split) {
                if (cabinetId.endsWith(end)) {
                    b = true;
                }
            }
        }
        return b;
    }
}
