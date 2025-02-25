package com.zzzde.game.machine.service;

import cn.net.yugu.doraemon.api.common.response.DataResult;
import cn.net.yugu.metis.api.model.HdRecommendBespeakQueryRequest;
import cn.net.yugu.metis.api.model.HdRecommendBespeakQueryResponse;
import com.zzzde.game.machine.domain.HdExchangeBespeakBatteryReqDTO;
import com.zzzde.game.machine.domain.HdMarkBespeakBatteryRspDTO;

/**
 * @Author zzzde
 * @Date 2023/11/13
 */
public interface HdRecommendBespeakService {

    DataResult<HdRecommendBespeakQueryResponse> recommendBatteriesBespeakQuery(HdRecommendBespeakQueryRequest recommendationRequest,Integer type);

    HdMarkBespeakBatteryRspDTO bespeakBatteryExchangeB(HdExchangeBespeakBatteryReqDTO hdExchangeBespeakBatteryReqDTO);
    HdMarkBespeakBatteryRspDTO bespeakBatteryExchangeC(HdExchangeBespeakBatteryReqDTO hdExchangeBespeakBatteryReqDTO);
}
