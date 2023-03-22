package com.zzzde.game.machine.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author zzzde
 * @version 1.0
 * @date 2023/3/22 14:01
 */
@Data
public class RollEntity {
    int money;
    double wPer;
    double gPer;
    double pPer;
    double oPer;
    double rPer;
    /**
     * 抽取方式
     */
    @Getter
    @AllArgsConstructor
    public enum RollType {
        LEVEL_1 ("level-1",100D),
        LEVEL_2 ("level-2",400D),
        LEVEL_3 ("level-3",1600D),
        LEVEL_4 ("level-4",6400D),
        LEVEL_5 ("level-5",25600D);
        private String name;
        private double consume ;
    }

    public double sumPer() {
        return wPer + gPer + pPer + oPer + rPer ;
    }
}
