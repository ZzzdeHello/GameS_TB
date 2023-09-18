package com.zzzde.game.springboot.my.mutile.mybatis.plus.service;

import com.zzzde.game.springboot.my.mutile.mybatis.plus.entity.User;

/**
 * @Author zzzde
 * @Date 2023/9/18
 */
public interface UserService {

    User findById(Long id);
}
