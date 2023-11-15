package com.zzzde.game.machine.controller;

import com.zzzde.game.machine.domain.HdExchangeBespeakBatteryReqDTO;
import com.zzzde.game.machine.domain.HdMarkBespeakBatteryRspDTO;
import com.zzzde.game.machine.service.HdRecommendBespeakService;
import com.zzzde.game.tb.common.dal.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Author zzzde
 * @Date 2023/10/10
 */
@Controller
@RequestMapping("/machine/hd")
public class HdRecommendControllerV1 {

    @Autowired
    HdRecommendBespeakService hdRecommendBespeakService;

    @PostMapping("recommend/v1")
    @ResponseBody
    public ResponseDTO<HdMarkBespeakBatteryRspDTO> hdRecommend(@RequestBody HdExchangeBespeakBatteryReqDTO dto) {
        HdMarkBespeakBatteryRspDTO hdMarkBespeakBatteryRspDTO = hdRecommendBespeakService.bespeakBatteryExchange(dto);
        ResponseDTO<HdMarkBespeakBatteryRspDTO> responseDTO = new ResponseDTO<>();
        responseDTO.setData(hdMarkBespeakBatteryRspDTO);
        responseDTO.setCode(1);
        responseDTO.setMessage("成功");
        return responseDTO;
    }
}
