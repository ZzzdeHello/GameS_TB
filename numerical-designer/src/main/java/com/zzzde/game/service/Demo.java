package com.zzzde.game.service;

import java.util.Random;

/**
 * @author zzzde
 * @version 1.0
 * @date 2023/3/24 8:55
 */
public class Demo {


    /**
     * 加减法、乘除法 简单的线性关系
     */
    void addSubtractFunction() {

    }

    /**
     * 幂函数 f(x) = x ^ i
     * i为常量
     *
     */
    void mFunction(){
        int x  = 0 ; // x 为点数
        int i = 2;
        double pow = Math.pow(x, i);
    }

    /**
     * 幂函数 f(x) = a / ( b- x)
     * 有额外应用
     *
     */
    void mFunction2(){
        int x  = 0 ; // x 为点数
        int a = -1;
        int b = 2 ;
        double pow = Math.pow( (b - x), a);
    }

    /**
     * 等差、等比数列
     */


    /**
     * 正态分布
     */
    void zfFunction(){
        // java 自带的正态分布
        Random random = new Random();
        double v = random.nextGaussian();
        System.out.println(v);

        int a = 0 ; int b = 0 ;
        // 产生 a,b 之间的正态分布
        double v1 = Math.sqrt(b) * random.nextGaussian() + a;
    }
}
