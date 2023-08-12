package com.hiwei.test.agent.helper;


/**
 * @Description: 雪花算法工具
 * @Author 疆戟
 * @Date 2023/8/5 15:12
 * @Version 1.0
 */
public final class SnowFlakeHelper {
    private final long epoch; // 起始时间戳，可以根据需求设定
    private final long workerIdBits = 5L;
    private final long dataCenterIdBits = 5L;
    private final long maxWorkerId = ~(-1L << workerIdBits);
    private final long maxDataCenterId = ~(-1L << dataCenterIdBits);
    private final long sequenceBits = 12L;
    private final long workerIdShift = sequenceBits;
    private final long dataCenterIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;
    private final long sequenceMask = ~(-1L << sequenceBits);

    private long workerId;
    private long dataCenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private static final SnowFlakeHelper INSTANCE;

    static {
        INSTANCE = new SnowFlakeHelper(1l, 1l, 1l);
    }

    private SnowFlakeHelper(long workerId, long dataCenterId, long epoch) {
        if (workerId < 0 || workerId > maxWorkerId) {
            throw new IllegalArgumentException("Worker ID must be between 0 and " + maxWorkerId);
        }
        if (dataCenterId < 0 || dataCenterId > maxDataCenterId) {
            throw new IllegalArgumentException("Datacenter ID must be between 0 and " + maxDataCenterId);
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
        this.epoch = epoch;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds.");
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;
        return ((timestamp - epoch) << timestampLeftShift) |
                (dataCenterId << dataCenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }


    public static SnowFlakeHelper getInstance() {
        return INSTANCE;
    }

}
