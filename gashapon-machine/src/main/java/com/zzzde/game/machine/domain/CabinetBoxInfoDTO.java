package com.zzzde.game.machine.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author zzzde
 * @Date 2023/11/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CabinetBoxInfoDTO implements Serializable {

    private static final long serialVersionUID = 7914405335229678410L;
    /**
     * 电柜ID
     */
    public String cabinetId;

    /**
     * 格口编号
     */
    public String boxNum;

    /**
     * 电池Id
     */
    public String batteryId;

    /**
     * 电池状态
     */
    public Integer status;

    /**
     * 电池类型
     */
    public Integer batteryType;
    public Integer batterySku;
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
     * 电池电量
     */
    public Integer batteryVolume;

    /**
     * 电池电压
     */
    private Integer batteryVoltage;

    /**
     * 电池容量
     */
    private Integer batteryCapacity;
}
