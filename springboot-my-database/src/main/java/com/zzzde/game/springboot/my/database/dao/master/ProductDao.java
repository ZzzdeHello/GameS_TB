package com.zzzde.game.springboot.my.database.dao.master;


import com.zzzde.game.springboot.my.database.entity.Product;

/**
 * @author zzzde
 * @version 1.0
 * @date 2023/4/26 15:48
 */
public interface ProductDao {

    Product queryById(Long id);

    int add(Product product);

    void deleteById(Long id);
}
