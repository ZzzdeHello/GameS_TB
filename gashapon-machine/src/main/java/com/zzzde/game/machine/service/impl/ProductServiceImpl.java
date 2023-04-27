package com.zzzde.game.machine.service.impl;

import com.zzzde.game.machine.service.IProductService;
import com.zzzde.game.springboot.my.database.dao.master.ProductDao;
import com.zzzde.game.springboot.my.database.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author zzzde
 * @version 1.0
 * @date 2023/4/26 15:05
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductDao dao;

    @Override
    public Product queryProductById(Long id) {
        System.out.println("--dao");
        return dao.queryById(id);
    }

    @Override
    public int addProduct(Product product) {
        return dao.add(product);
    }

    @Override
    public int deleteProductById(Long id) {
        return dao.deleteById(id);
    }
}
