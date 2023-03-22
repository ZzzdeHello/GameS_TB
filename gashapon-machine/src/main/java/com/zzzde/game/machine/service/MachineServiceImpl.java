package com.zzzde.game.machine.service;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zzzde.game.machine.IService.IMachineService;
import com.zzzde.game.machine.entity.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author zzzde
 * @version 1.0
 * @date 2023/3/22 10:57
 */
@Service
public class MachineServiceImpl implements IMachineService {

    @Override
    public int getHero(HeroTemplate template) throws IOException {
        String localDir = System.getProperty("user.dir");
        File distribution = new File(localDir + "\\src\\main\\resources\\hero-distribution.json");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(distribution), StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();
        int ch;
        while ((ch = inputStreamReader.read()) != -1) {
            sb.append((char) ch);
        }
        String HeroDistributionJson = sb.toString();
        HeroDistribution heroDistribution = JSON.parseObject(HeroDistributionJson, HeroDistribution.class);
        HeroQuality color = template.getColor();
        int[] heroPool = new int[]{};
        if (color.equals(HeroQuality.WHITE)) heroPool = heroDistribution.getWhite();
        if (color.equals(HeroQuality.GREEN)) heroPool = heroDistribution.getGreen();
        if (color.equals(HeroQuality.PURPLE)) heroPool = heroDistribution.getPurple();
        if (color.equals(HeroQuality.ORANGE)) heroPool = heroDistribution.getOrange();
        if (color.equals(HeroQuality.RED)) heroPool = heroDistribution.getRed();
        int i = RandomUtil.randomInt(0, heroPool.length);
        return heroPool[i];
    }

    @Override
    public HeroTemplate getHeroTemplate(RollEntity.RollType rollLevel) throws IOException {

        String localDir = System.getProperty("user.dir");
        File probability = new File(localDir + "\\src\\main\\resources\\hero-probability.json");
        InputStreamReader probabilityStreamReader = new InputStreamReader(new FileInputStream(probability), StandardCharsets.UTF_8);
        StringBuilder sb2 = new StringBuilder();
        int ch1;
        while ((ch1 = probabilityStreamReader.read()) != -1) {
            sb2.append((char) ch1);
        }
        String probabilityJson = sb2.toString();
        JSONObject jsonObject = JSON.parseObject(probabilityJson);
        RollEntity level = jsonObject.getObject(rollLevel.getName(), RollEntity.class);
        return lottery(level);
    }

    public HeroTemplate lottery(RollEntity roll) {
        HeroTemplate w = new HeroTemplate();
        w.setColor(HeroQuality.WHITE).setRollWeight(roll.getWPer());
        HeroTemplate g = new HeroTemplate();
        g.setColor(HeroQuality.GREEN).setRollWeight(roll.getGPer());
        HeroTemplate p = new HeroTemplate();
        p.setColor(HeroQuality.PURPLE).setRollWeight(roll.getPPer());
        HeroTemplate o = new HeroTemplate();
        o.setColor(HeroQuality.ORANGE).setRollWeight(roll.getOPer());
        HeroTemplate r = new HeroTemplate();
        r.setColor(HeroQuality.RED).setRollWeight(roll.getRPer());
        List<HeroTemplate> list = new ArrayList<>();
        list.add(w);
        list.add(g);
        list.add(o);
        list.add(r);
        list.add(p);
        List<Integer> nodeList = new ArrayList<>();
        //计算节点 节点的数量比奖品的数量多一个，即0
        nodeList.add(0);
        for (HeroTemplate heroTemplate : list) {
            nodeList.add(nodeList.get(nodeList.size() - 1) + heroTemplate.getRollWeight().intValue());
        }
        //生成 0-结束节点 的随机数
        int randomInt = RandomUtil.randomInt(0, nodeList.get(nodeList.size() - 1));
        //最终抽奖逻辑 此处需要从第二个节点开始遍历
        for (int i = 1; i < nodeList.size(); i++) {
            //本次节点
            Integer endNode = nodeList.get(i);
            //前一个节点
            Integer startNode = nodeList.get(i - 1);
            //若随机数大于等于前一个节点并且小于本节点，在prizeList中位于i-1位置的奖品为抽中奖品
            //Tip：比较大小时，左闭右开与左开右闭都可以，不影响整体概率
            if (randomInt >= startNode
                    && randomInt < endNode) {
                return list.get(i - 1);
            }
        }
        throw new RuntimeException("程序异常 生成的随机数不在任何奖品区间内");
    }


}
