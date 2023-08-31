package com.zzzde.game.machine.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 所有角色模板类
 *
 * @author zzzde
 * @version 1.0
 * @date 2023/3/22 9:39
 */
@Data
@Accessors(chain = true)
public class HeroTemplate {

//基础属性
    /**
     * 力
     */
    Integer strength;

    /**
     * 敏
     */
    Integer agility;

    /**
     * 智
     */
    Integer intelligence;

    /**
     * 攻击频率
     */
    Integer attackFrequency;

    /**
     * 物理防御
     */
    Integer physicalDefense;

    /**
     * 法术防御
     */
    Integer magicDefense;


//    羁绊
    /**
     * 职业
     */
    String occupation;

    /**
     * 种族
     */
    String race;

//    技能
    /**
     * 基础技能
     */
    String skillQ;

    /**
     * 被动技能
     */
    String skillE;

    /**
     * 核心技能
     */
    String skillR;

    /**
     * 卡品质
     */
    HeroQuality color ;

    /**
     * 抽卡权重
     */
    Double rollWeight ;
}
