package com.zh.physiology.assist;

import java.math.BigDecimal;

/**
 * author：heng.zhang
 * date：2017/3/11
 * description：
 */
public class StringUtil {
    /**
     * float 转成一位小数
     *
     * @param value
     * @return
     */
    public static String floatFormat(float value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(1, BigDecimal.ROUND_HALF_DOWN);
        return bd.toString();
    }
}
