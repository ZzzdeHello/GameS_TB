package com.zzzde.game.springboot.my.database.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zzzde
 * @version 1.0
 * @date 2023/4/26 15:07
 */
@Data
public class Product implements Serializable {

    Long id;

    String name;

    Integer price;

    Date createDate;

}
