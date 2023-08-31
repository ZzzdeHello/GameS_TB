package com.zzzde.game.machine.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author zzzde
 * @version 1.0
 * @date 2023/3/22 16:46
 */
@Getter
@AllArgsConstructor
public enum HeroQuality {
    WHITE (1,"white"),
    GREEN (2,"green"),
    PURPLE (3,"purple"),
    ORANGE (4,"orange"),
    RED (5,"red");
    private int typeNum;
    private String color ;
}
