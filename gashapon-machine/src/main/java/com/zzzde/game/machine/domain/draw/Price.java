package com.zzzde.game.machine.domain.draw;

import lombok.Builder;
import lombok.Data;

/**
 * @Author zzzde
 * @Date 2023/11/23
 */
@Data
@Builder
public class Price {

    private Long id;

    private String name;

    private Double percent;
}
