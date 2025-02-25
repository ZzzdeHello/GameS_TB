package com.zzzde.game.machine.service;

import com.zzzde.game.machine.domain.draw.Price;

import java.util.List;

/**
 * @Author zzzde
 * @Date 2023/11/23
 */
public interface GetOneLotteryService {

    Long getOne(List<Price> priceList);
}
