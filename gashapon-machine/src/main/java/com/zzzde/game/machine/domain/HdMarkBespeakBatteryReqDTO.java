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
public class HdMarkBespeakBatteryReqDTO implements Serializable {

    private static final long serialVersionUID = 7437289278469066434L;

    /**
     * 查询系统来源
     * 1、骑手端；2、平台端；3 运维端 ；4未知
     */
    private Integer systemSource;

    /**
     * 查询人ID.
     */
    private Long observer;

    /**
     * 查询时间戳
     */
    private Long queryTimestamp;

    /**
     * 柜子Id
     */
    public String cabinetId;

    /**
     * 需要重新计算的柜子信息详情集合
     */
    List<CabinetBoxInfoDTO> cabinetBoxInfoDTOList;
}
