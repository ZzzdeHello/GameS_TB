package com.zzzde.game.machine;

import com.alibaba.fastjson.JSON;
import com.zzzde.game.machine.entity.User;
import com.zzzde.game.machine.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

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

    @Test
    public void test(){
        User user = userServiceImpl.getUser();
        System.out.println(JSON.toJSONString(user));
    }
}
