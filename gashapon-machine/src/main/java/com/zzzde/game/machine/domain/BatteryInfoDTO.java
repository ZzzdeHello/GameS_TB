package com.zzzde.game.machine.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author wanghongli
 * @date 2023/8/15 13:18
 * @description BatteryInfoDTO
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatteryInfoDTO {
    /**
     * 电池id.
     */
    private String batteryId;
    /**
     * 对应格口信息.
     */
    private String boxNum;
    /**
     * 电量.
     */
    private Integer volume;
    /**
     * 容量.
     */
    private Integer capacity;

    /**
     * 电池类型
     */
    private Integer batteryType;

    /**
     * 电池sku
     */
    private Integer batterySku;

    /**
     * 额外参数信息.
     */
    private Map<String, String> extra;
}
