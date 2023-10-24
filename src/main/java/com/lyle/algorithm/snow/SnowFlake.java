package com.lyle.algorithm.snow;

import com.lyle.algorithm.bit.MaxNum;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.concurrent.CountDownLatch;

/**
 * flakes of snow 雪花
 * flake: 小薄片;（尤指）碎片
 *
 *             |-----------------41bit时间戳-----------------|                 |-12bit序列号-|
 *         0 - 00000000 00000000 00000000 00000000 00000000 0 - 00000000 00 - 00000000 0000
 *         |                                                    |---------|
 *       1bit不用                                          10bit 工作机器ID/机房ID+机器
 *
 * 41位的时间戳：  (2^41−1)/(1000∗60∗60∗24∗365)=69年；41位时间截不是存储当前时间的时间戳，而是存储时间戳的差值（当前时间戳 - 开始时间戳)
 * 10位的机器ID： 2^10=1024台机器。（一般是5位数据中心，5位机器标识）
 * 12位的序列号： 2^12=4096个数
 *
 */
public class SnowFlake {

    private final long workerId; //工作机器ID
    private final long dataCenterId; //数据中心ID
    /**
     * 起初第一次运行使用0，最后一次使用4095，共 4096个
     */
    private long sequence = 0; //1毫秒内序号(0~4095)

    /**
     * 开始时间截 (2023-10-24)
     */
    private static final long twEpoch = 1698076800000L;
    private static final int sequenceBits = 12;//序列id所占位数
    private static final int workerIdBits = 5;//机器ID所占位数
    private static final int dataCenterIdBits = 5;//数据中心所占位数

    /**
     * 最大机器ID: 31
     */
    private static final long maxWorkerId = MaxNum.maxNumByXBits(workerIdBits);
    //最大数据中心ID
    private static final long maxDataCenterId = MaxNum.maxNumByXBits(dataCenterIdBits);
    /**
     * 最大序列号: 4095
     */
    private final long maxSequence = MaxNum.maxNumByXBits(sequenceBits);


    /**
     *             |-----------------41bit时间戳-----------------|                 |-12bit序列号-|
     *         0 - 00000000 00000000 00000000 00000000 00000000 0 - 00000000 00 - 00000000 0000
     *         |                                                    |---------|
     *       1bit不用                                          10bit 工作机器ID/机房ID+机器ID
     */

    /**
     * 时间截向左移22位(5+5+12)
     */
    private static final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;
    /**
     * 数据标识id向左移17位(12+5)
     */
    private static final long dataCenterIdShift = sequenceBits + workerIdBits;
    /**
     * 机器ID向左移12位
     */
    private static final long workerIdShift = sequenceBits;

    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    public SnowFlake(int workerId, int dataCenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("workerId can't be greater than %d or less than 0", maxWorkerId));
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("dataCenterId can't be greater than %d or less than 0", maxDataCenterId));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    public synchronized long nextId(){
        long currentMills = currentMills();
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (currentMills < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.Refusing to generate id for %d milliseconds", lastTimestamp - currentMills));
        }
        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == currentMills) {
            sequence = (sequence + 1) & maxSequence;
            // 毫秒内序列溢出
            //sequence == 0 ，就是1毫秒用完了4096个数
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                currentMills = tilNextMillis(lastTimestamp);
            }
        } else { // 时间戳改变，毫秒内序列重置
            sequence = 0L;
        }
        // 上次生成ID的时间截
        lastTimestamp = currentMills;

        // 移位并通过或运算拼到一起组成64位的ID
        return ((currentMills - twEpoch) << timestampLeftShift) // 时间戳左移22位
                | (dataCenterId << dataCenterIdShift) //数据标识左移17位
                | (workerId << workerIdShift) //机器id标识左移12位
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long currentMills = currentMills();
        while (currentMills <= lastTimestamp) {
            currentMills = currentMills();
        }
        return currentMills;
    }
    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    protected long currentMills() {
        return System.currentTimeMillis();
    }

    /**
     * 1698076800000 ===> 2023-10-24
     */
    private static long getTwEpoch(){
        return LocalDate.now().atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
    }


    public static void main(String[] args) {
        SnowFlake snowFlake = new SnowFlake(9, 20);
        CountDownLatch cdl = new CountDownLatch(1000);
        for (int i = 0; i < 1000; i++) {
            new Thread(()->{
                try {
                    cdl.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(snowFlake.nextId());
            }).start();
            cdl.countDown();
        }
    }

    /**
     * 算法最后移位原理说明:
     *
     * 时间戳位数41
     * //时间戳------------------------------------//
     * 0000000000000000000000010111111111100000101101001000000000000000
     * //数据标识------------------------------------//
     * 0000000000000000000000000000000000000000000000000000000000000101
     * //机器标识------------------------------------//
     * 0000000000000000000000000000000000000000000000000000000000000110
     * //自增数------------------------------------//
     * 0000000000000000000000000000000000000000000000000000000000000001
     *
     * //位移22位后时间戳------------------------------------//
     * 0101111111111000001011010010000000000000000000000000000000000000
     * //位移17位后数据标识------------------------------------//
     * 0000000000000000000000000000000000000000000010100000000000000000
     * //位移12位后机器标识------------------------------------//
     * 0000000000000000000000000000000000000000000000000110000000000000
     * //自增数（最后一个部分不用位移）------------------------------------//
     * 0000000000000000000000000000000000000000000000000000000000000001
     *
     * 计算合并
     *          0101111111111000001011010010000000000000000000000000000000000000  //时间戳
     *     |    0000000000000000000000000000000000000000000010100000000000000000  //数据标识
     *     |    0000000000000000000000000000000000000000000000000110000000000000  //机器标识
     *     |    0000000000000000000000000000000000000000000000000000000000000001  //数据标识
     * ---------------------------------------------------------------------
     *          0101111111111000001011010010000000000000000010100110000000000001  //64位
     *
     * 最后可以看出，对应每个部分之间的数据就是互不影响，放在了各自对应的位数范围内，把每个部分的数据合并起来
     */
}