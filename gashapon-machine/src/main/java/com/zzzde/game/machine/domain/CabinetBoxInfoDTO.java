package com.zzzde.game.machine.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author zzzde
 * @Date 2023/11/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CabinetBoxInfoDTO extends BatteryInfoDTO implements Serializable {

    private static final long serialVersionUID = 7914405335229678410L;

    /**
     * 电柜ID
     */
    public String cabinetId;

    /**
     * 电池状态
     */
    public Integer status;

    public Long customerId;

    public String orderId;

    public Long bespeakCustomerId;

    public String bespeakOrderId;

    public Integer bespeakType;

    public Integer isActive;

    /**
     * 格口状态 1 空箱 2 满箱 3 空箱锁定 4 满箱锁定'
     */
    public Integer boxStatus;

    public Integer isOpen;

    public Integer lockType;

    public Long lockUserId;

    /**
     * 伪预约标识
     */
    private Integer fakeBespeak;
}
