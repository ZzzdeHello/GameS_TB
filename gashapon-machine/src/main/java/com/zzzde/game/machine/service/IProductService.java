package com.zzzde.game.machine.service;

import com.zzzde.game.springboot.my.database.entity.Product;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

/**
 * value、等于 cacheNames
 *
 * @author zzzde
 * @version 1.0
 * @date 2023/4/26 15:06
 */
@CacheConfig(cacheNames = "cache::product")
public interface IProductService {

    @CachePut(key = "'product::' + #product.id")
    int addProduct(Product product);

    @Cacheable(key = "'product::' + #id")
    Product queryProductById(Long id);

    @CacheEvict(key = "'product::' + #id")
    void deleteProductById(Long id);
}
