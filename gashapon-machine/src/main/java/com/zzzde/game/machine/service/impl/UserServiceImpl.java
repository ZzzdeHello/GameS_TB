package com.zzzde.game.machine.service.impl;

import com.zzzde.game.machine.service.IUserService;
import com.zzzde.game.springboot.my.database.dao.master.UserDao;
import com.zzzde.game.springboot.my.database.entity.User;
import com.zzzde.game.springboot.my.redis.IRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author zzzde
 * @date 2022/12/10
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IRedisService redisService;

    private final UserDao userDao;

    @Override
    public User  getUser () {
        return userDao.testQuery(1L);
    }

    @Override
    public void  setCacheUser (String value) {
        redisService.set("setCacheUser:test",value,600L);
    }
}
