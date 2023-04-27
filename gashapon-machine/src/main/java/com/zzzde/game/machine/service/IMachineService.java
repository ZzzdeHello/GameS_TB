package com.zzzde.game.machine.service;

import com.zzzde.game.machine.domain.HeroTemplate;
import com.zzzde.game.machine.domain.RollEntity;

import java.io.IOException;

/**
 * @author zzzde
 * @version 1.0
 * @date 2023/3/22 13:57
 */
public interface IMachineService {

    int getHero(HeroTemplate template) throws IOException;

    HeroTemplate getHeroTemplate(RollEntity.RollType rollLevel) throws IOException;
}
