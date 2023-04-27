package com.zzzde.game.machine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author zzzde
 * @version 1.0
 * @date 2023/3/21 11:26
 */
@SpringBootApplication(scanBasePackages = {"com.zzzde.game.**"})
@EnableCaching
public class GashaponMachine {
    public static void main(String[] args) {
        SpringApplication.run(GashaponMachine.class, args);
    }
}


