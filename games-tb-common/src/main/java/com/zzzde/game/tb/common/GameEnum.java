package com.zzzde.game.tb.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author zzzde
 * @Date 2023/8/31
 */
public class GameEnum {

    /**
     * 状态
     */
    public enum Status {
        GOOD(-1, "正常"),
        BAD(1, "错误"),
        ;
        private final int value;
        private final String name;

        Status(int value, String name) {
            this.value = value;
            this.name = name;
        }

        private static Map<Integer, String> map = new HashMap<Integer, String>();

        static {
            for (Status e : Status.values()) {
                map.put(e.getValue(), e.getName());
            }
        }

        public static String getName(int value) {
            return map.get(value);
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }
}
