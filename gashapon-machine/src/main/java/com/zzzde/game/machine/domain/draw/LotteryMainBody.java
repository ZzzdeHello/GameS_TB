package com.zzzde.game.machine.domain.draw;

import lombok.Builder;
import lombok.Data;

/**
 * @Author zzzde
 * @Date 2023/11/23
 */
@Data
@Builder
public class LotteryMainBody {

    private Integer index;
    private Long priceId;
    private Double score;
}
