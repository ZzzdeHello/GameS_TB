package com.zzzde.game.machine.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zzzde.game.machine.domain.CabinetBatteryListResponse;
import com.zzzde.game.machine.domain.HdExchangeBespeakBatteryReqDTO;
import com.zzzde.game.machine.domain.HdMarkBespeakBatteryRspDTO;
import com.zzzde.game.machine.service.HdRecommendBespeakService;
import com.zzzde.game.tb.common.dal.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Author zzzde
 * @Date 2023/10/10
 */
@Controller
@RequestMapping("/machine/hd")
public class HdRecommendControllerV1 {

    @Value("${metis.bespeak.cabinet.end.rule:1,2,3,4,5}")
    private String bespeakCabinetEndRule;

    @Autowired
    HdRecommendBespeakService hdRecommendBespeakService;

    @PostMapping("recommend/v1")
    @ResponseBody
    public ResponseDTO<HdMarkBespeakBatteryRspDTO> hdRecommend(@RequestBody HdExchangeBespeakBatteryReqDTO dto) {
        ResponseDTO<HdMarkBespeakBatteryRspDTO> responseDTO = new ResponseDTO<>();
        if (!enableFilterCabinetByConfig(dto.getCabinetId(),bespeakCabinetEndRule)){
            responseDTO.setCode(1);
            responseDTO.setMessage("成功");
            return responseDTO;
        }
        HdMarkBespeakBatteryRspDTO  hdMarkBespeakBatteryRspDTO ;
        if (dto.getType().equals(1)){
             hdMarkBespeakBatteryRspDTO = hdRecommendBespeakService.bespeakBatteryExchangeB(dto);
        }else {
            hdMarkBespeakBatteryRspDTO = hdRecommendBespeakService.bespeakBatteryExchangeC(dto);
        }
        responseDTO.setData(hdMarkBespeakBatteryRspDTO);
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
