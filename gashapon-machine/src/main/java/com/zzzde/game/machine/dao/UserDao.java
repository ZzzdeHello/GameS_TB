package com.zzzde.game.machine.dao;

import com.zzzde.game.machine.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author zzzde
 * @version 1.0
 * @date 2023/3/21 16:43
 */
public interface UserDao {

    User testQuery(Long id);
}
