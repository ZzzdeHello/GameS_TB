package com.zzzde.game.springboot.my.mutile.mybatis.plus.dao.master;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzzde.game.springboot.my.mutile.mybatis.plus.entity.User;

/**
 * @author zzzde
 * @version 1.0
 * @date 2023/3/21 16:43
 */
public interface UserDao extends BaseMapper<User> {

    User testQuery(Long id);
}
