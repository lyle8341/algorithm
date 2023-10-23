package com.lyle.algorithm.snow;

/**
 * flakes of snow 雪花
 * flake: 小薄片;（尤指）碎片
 *
 *             |-----------------41bit时间戳-----------------|                 |-12bit序列号-|
 *         0 - 00000000 00000000 00000000 00000000 00000000 0 - 00000000 00 - 00000000 0000
 *         |                                                    |---------|
 *       1bit不用                                          10bit 工作机器ID/机房ID+机器
 *
 * 41位的时间戳：  (2^41−1)/(1000∗60∗60∗24∗365)=69年
 * 10位的机器ID： 2^10=1024台机器。（一般是5位数据中心，5位机器标识）
 * 12位的序列号： 2^12=4096个数
 *
 */
public class SnowFlake {








    public static void main(String[] args) {
        long a = -1L ^ (-1L << 6);
        System.out.println("a = " + a);
        long b = ~(-1L << 6);

        System.out.println("b = " + b);
    }


















}
