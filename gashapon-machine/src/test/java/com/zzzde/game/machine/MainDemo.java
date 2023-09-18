package com.zzzde.game.machine;

import com.zzzde.game.springboot.my.mutile.mybatis.plus.entity.User;
import com.zzzde.game.springboot.my.mutile.mybatis.plus.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zzzde
 * @version 1.0
 * @date 2023/3/21 16:55
 */
@Slf4j
@SpringBootTest
@DisplayName("测试")
public class MainDemo {

    @Autowired
    private UserService userService;

    @Test
    public void test(){
        User byId = userService.findById(498L);
        System.out.println(byId);
    }
}
