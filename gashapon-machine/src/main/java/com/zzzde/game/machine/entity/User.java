package com.zzzde.game.machine.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author zzzde
 * @date 2022/12/6
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class User {
    private Long id;
    private String mobile;
    private String username;
    private String fullname;
    private Date loginTime;
}