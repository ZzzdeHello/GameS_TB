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
public class HdMarkBespeakBatteryRspDTO implements Serializable {

    private static final long serialVersionUID = 2368017222686133968L;
    /**
     * 标记为伪预约状态的电池集合
     */
    public List<BatteryInfoDTO> batteryList;
}
