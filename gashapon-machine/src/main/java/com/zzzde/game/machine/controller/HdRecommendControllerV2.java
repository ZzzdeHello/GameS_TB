package com.zzzde.game.machine.controller;

import cn.net.yugu.doraemon.api.common.response.DataResult;
import cn.net.yugu.metis.api.model.BatteryInfo;
import cn.net.yugu.metis.api.model.HdRecommendBespeakQueryRequest;
import cn.net.yugu.metis.api.model.HdRecommendBespeakQueryResponse;
import com.alibaba.fastjson.JSON;
import com.zzzde.game.machine.domain.CabinetBatteryListResponse;
import com.zzzde.game.machine.domain.HdExchangeBespeakBatteryReqDTO;
import com.zzzde.game.machine.domain.HdMarkBespeakBatteryRspDTO;
import com.zzzde.game.machine.service.HdRecommendBespeakService;
import com.zzzde.game.tb.common.dal.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    HdRecommendBespeakService hdRecommendBespeakService;

    @PostMapping("recommend/v2")
    @ResponseBody
    public ResponseDTO<CabinetBatteryListResponse> hdRecommend(@RequestBody CabinetBatteryListResponse response) {

        List<BatteryInfo> fakeBatteryList = new ArrayList<>();

        HdRecommendBespeakQueryRequest hdRecommendBespeakQueryRequest = new HdRecommendBespeakQueryRequest();
        hdRecommendBespeakQueryRequest.setObserver(1L);
        hdRecommendBespeakQueryRequest.setCabinetId("gz001");
        hdRecommendBespeakQueryRequest.setSystemSource(1);
        hdRecommendBespeakQueryRequest.setQueryTimestamp(System.currentTimeMillis());
        hdRecommendBespeakQueryRequest.setBatteryList(response.batteryList.stream().map(x->{
            HdRecommendBespeakQueryRequest.Battery battery = new HdRecommendBespeakQueryRequest.Battery();
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
        hdRecommendBespeakQueryRequest.setCabinetBoxList(response.boxList.stream().map(y->{
            HdRecommendBespeakQueryRequest.CabinetBox cabinetBox = new HdRecommendBespeakQueryRequest.CabinetBox();
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
        DataResult<HdRecommendBespeakQueryResponse> queryResponseDataResult = hdRecommendBespeakService.recommendBatteriesBespeakQuery(hdRecommendBespeakQueryRequest);
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
        ResponseDTO<CabinetBatteryListResponse> responseDTO = new ResponseDTO<>();
        System.out.println("--------------------------------------------------" + JSON.toJSONString(response));
        responseDTO.setData(response);
        responseDTO.setCode(1);
        responseDTO.setMessage("成功");
        return responseDTO;
    }
}
