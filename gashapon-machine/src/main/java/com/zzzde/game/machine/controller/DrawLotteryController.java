package com.zzzde.game.machine.controller;

import com.zzzde.game.machine.domain.DrawLotteryReqDTO;
import com.zzzde.game.machine.domain.draw.Price;
import com.zzzde.game.machine.service.GetOneLotteryService;
import com.zzzde.game.tb.common.ResponseEnum;
import com.zzzde.game.tb.common.dal.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Author zzzde
 * @Date 2023/11/23
 */
@Controller
@RequestMapping("/draw/lottery")
public class DrawLotteryController {

    @Autowired
    GetOneLotteryService getOneLotteryService;

    @PostMapping("getOne")
    @ResponseBody
    public ResponseDTO<Long> testLimit(@RequestBody DrawLotteryReqDTO reqDTO) {
        ResponseDTO<Long> dto = new ResponseDTO<>();
        Long one = getOneLotteryService.getOne(reqDTO.getPriceList());
        dto.setCode(ResponseEnum.OK.getCode());
        dto.setMessage(ResponseEnum.OK.getMessage());
        dto.setData(one);
        return dto;
    }
}
