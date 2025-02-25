package com.zzzde.game.machine.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.zzzde.game.machine.domain.draw.LotteryMainBody;
import com.zzzde.game.machine.domain.draw.Price;
import com.zzzde.game.machine.service.GetOneLotteryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author zzzde
 * @Date 2023/11/23
 */
@Service
public class GetOneLotteryServiceImpl implements GetOneLotteryService {


    @Override
    public Long getOne(List<Price> priceList) {
        List<LotteryMainBody> lotteryMainBodies = new ArrayList<>();
        double sum = 0D;
        for (int i = 0; i < priceList.size(); i++) {
            sum += priceList.get(i).getPercent();
            if (sum <= 1) {
                Long priceId = priceList.get(i).getId();
                LotteryMainBody lotteryMainBody = LotteryMainBody.builder().index(i).score(sum).priceId(priceId).build();
                lotteryMainBodies.add(lotteryMainBody);
            }
        }
        // 放入谢谢惠顾抽奖对象
        if (sum <= 1) {
            LotteryMainBody lotteryMainBody = LotteryMainBody.builder().index(lotteryMainBodies.size()).score(1 - sum).priceId(0L).build();
            lotteryMainBodies.add(lotteryMainBody);
        }
        double i = RandomUtil.randomDouble(0, 1);
        for (LotteryMainBody lotteryMainBody : lotteryMainBodies) {
            if (i <= lotteryMainBody.getScore()) {
                return lotteryMainBody.getPriceId();
            }
        }
        return 0L;
    }
}
