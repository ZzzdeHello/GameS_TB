package com.zzzde.game.machine.service;

import com.zzzde.game.springboot.my.database.entity.User;

/**
 * @author zzzde
 * @version 1.0
 * @date 2023/3/21 16:59
 */
public interface IUserService {
    User getUser();

    void setCacheUser(String value);
}
