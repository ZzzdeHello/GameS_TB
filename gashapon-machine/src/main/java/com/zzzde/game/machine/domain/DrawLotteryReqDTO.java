package com.zzzde.game.machine.domain;

import com.zzzde.game.machine.domain.draw.Price;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zzzde
 * @Date 2023/11/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrawLotteryReqDTO implements Serializable {

    List<Price> priceList;
}
