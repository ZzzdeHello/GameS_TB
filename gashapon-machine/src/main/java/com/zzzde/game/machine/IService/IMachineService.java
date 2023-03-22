package com.zzzde.game.machine.IService;

import com.zzzde.game.machine.entity.Hero;
import com.zzzde.game.machine.entity.HeroTemplate;
import com.zzzde.game.machine.entity.RollEntity;

import java.io.IOException;
import java.util.List;

/**
 * @author zzzde
 * @version 1.0
 * @date 2023/3/22 13:57
 */
public interface IMachineService {

    int getHero(HeroTemplate template) throws IOException;

    HeroTemplate getHeroTemplate(RollEntity.RollType rollLevel) throws IOException;
}
