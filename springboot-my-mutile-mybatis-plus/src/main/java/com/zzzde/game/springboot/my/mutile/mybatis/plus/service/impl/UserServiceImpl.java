package com.zzzde.game.springboot.my.mutile.mybatis.plus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzzde.game.springboot.my.mutile.mybatis.plus.dao.master.UserDao;
import com.zzzde.game.springboot.my.mutile.mybatis.plus.entity.User;
import com.zzzde.game.springboot.my.mutile.mybatis.plus.service.UserService;
import org.springframework.stereotype.Component;

/**
 * @Author zzzde
 * @Date 2023/9/18
 */
@Component
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    @Override
    public User findById(Long id) {
        return this.getById(id);
    }
}
