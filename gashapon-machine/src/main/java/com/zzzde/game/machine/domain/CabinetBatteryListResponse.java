package com.zzzde.game.machine.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CabinetBatteryListResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Battery {
        public String boxNum;
        public Integer status;
        public String batteryId;
        public Integer batteryType;
        public Long customerId;
        public String orderId;
        public Long bespeakCustomerId;
        public String bespeakOrderId;
        public Integer bespeakType;
        public Integer fakeBespeak;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CabinetBox {
        public String cabinetId;
        public String boxNum;
        public Integer isActive;
        public Integer boxStatus; //格口状态 1 空箱 2 满箱 3 空箱锁定 4 满箱锁定'
        public Integer isOpen;
        public Integer lockType;
        public Long lockUserId;
        public String batteryId;
        public Integer batteryVolume;
        public Integer batteryVoltage;
        public Integer batteryCapacity;
    }

    public String cabinetId;
    public Integer type;
    public List<Battery> batteryList = new ArrayList<>();
    public List<CabinetBox> cabinetBoxList = new ArrayList<>();
}
