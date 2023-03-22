package com.zzzde.game.machine;

import com.alibaba.fastjson.JSON;
import com.zzzde.game.machine.IService.IMachineService;
import com.zzzde.game.machine.entity.HeroDistribution;
import com.zzzde.game.machine.entity.HeroTemplate;
import com.zzzde.game.machine.entity.RollEntity;
import com.zzzde.game.machine.entity.User;
import com.zzzde.game.machine.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author zzzde
 * @version 1.0
 * @date 2023/3/21 16:55
 */
@Slf4j
@SpringBootTest
@DisplayName("测试主类")
public class MainTest {

    @Resource
    private UserServiceImpl userServiceImpl;

    @Resource
    private IMachineService machineService;

    @Test
    public void test() {
        User user = userServiceImpl.getUser();
        System.out.println(JSON.toJSONString(user));
    }

    @Test
    public void test1() {
        int numW = 0;
        int numG = 0;
        int numP = 0;
        int numO = 0;
        int numR = 0;
        for (int i = 0; i < 100; i++) {
            HeroTemplate hero = null;
            try {
                hero = machineService.getHero(RollEntity.RollType.LEVEL_2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (hero != null) {
                switch (hero.getColor()) {
                    case "green":
                        numG += 1;
                        break;
                    case "white":
                        numW += 1;
                        break;
                    case "purple":
                        numP += 1;
                        break;
                    case "orange":
                        numO += 1;
                        break;
                    case "red":
                        numR += 1;
                        break;
                }
            }
        }
        System.out.println("-white: " + numW);
        System.out.println("-green: " + numG);
        System.out.println("-purple: " + numP);
        System.out.println("-orange: " + numO);
        System.out.println("-red: " + numR);
    }
}
