package com.zzzde.game.springboot.my.database.dao.master;

import com.zzzde.game.springboot.my.database.entity.User;

/**
 * @author zzzde
 * @version 1.0
 * @date 2023/3/21 16:43
 */
public interface UserDao {

    User testQuery(Long id);
}
