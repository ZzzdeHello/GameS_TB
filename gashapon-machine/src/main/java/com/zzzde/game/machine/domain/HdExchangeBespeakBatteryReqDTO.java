package com.zzzde.game.machine.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zzzde
 * @Date 2023/11/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HdExchangeBespeakBatteryReqDTO implements Serializable {

    private static final long serialVersionUID = 2516816828419633470L;
    /**
     * 需要重新计算的电池详情集合
     */
    List<BatteryInfoDTO> batteryInfoList;


    Integer type;


    /**
     * 骑手ID.
     */
    private Long customerId;

    /**
     * 柜子Id
     */
    public String cabinetId;
}
