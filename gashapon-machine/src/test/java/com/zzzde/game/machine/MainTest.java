package com.zzzde.game.machine;

import com.alibaba.fastjson.JSON;
import com.zzzde.game.machine.service.IMachineService;
import com.zzzde.game.machine.domain.HeroTemplate;
import com.zzzde.game.machine.domain.RollEntity;
import com.zzzde.game.machine.service.IProductService;
import com.zzzde.game.machine.service.IUserService;
import com.zzzde.game.springboot.my.database.entity.Product;
import com.zzzde.game.springboot.my.database.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;

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
    private IProductService productService;

    @Resource
    private IUserService userServiceImpl;

    @Resource
    private IMachineService machineService;

    @Test
    public void testCache(){
        Product product = productService.queryProductById(1L);
        System.out.println(product);
    }

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
                hero = machineService.getHeroTemplate(RollEntity.RollType.LEVEL_2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (hero != null) {
                switch (hero.getColor()) {
                    case GREEN:
                        numG += 1;
                        break;
                    case WHITE:
                        numW += 1;
                        break;
                    case PURPLE:
                        numP += 1;
                        break;
                    case ORANGE:
                        numO += 1;
                        break;
                    case RED:
                        numR += 1;
                        break;
                }
            }
            try {
                int hero1 = machineService.getHero(hero);
                System.out.println("抽到的hero的ID: -" + hero1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("-white: " + numW);
        System.out.println("-green: " + numG);
        System.out.println("-purple: " + numP);
        System.out.println("-orange: " + numO);
        System.out.println("-red: " + numR);
    }
}
