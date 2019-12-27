package com.zhm.service;

import com.zhm.util.GuidBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * <p>
 * 产生long类型的唯一id，基于Twitter的snow flake算法实现，单台机器每毫秒支持2^12=4096个id
 *
 * <p>
 * 第1位为0，符号位。第2-42位表示毫秒数，共41位，
 * 当前时间毫秒-2017年04月01日的毫秒数。
 * 第43-52位表示workId，
 * 即机器id，共10位，能支持1024台机器。
 * 第53-64位表示序列号，共12位
 */
@Service
public class OpenService {

    public static final long START_TIME_MILLIS;

    private static final long SEQUENCE_BITS = 12L; // 12位序列号

    private static final long WORKER_ID_BITS = 10L; // 10位workId号

    private static final long SEQUENCE_MASK = (1 << SEQUENCE_BITS) - 1;

    private static final long WORKER_ID_LEFT_SHIFT_BITS = SEQUENCE_BITS;

    private static final long TIMESTAMP_LEFT_SHIFT_BITS = WORKER_ID_LEFT_SHIFT_BITS + WORKER_ID_BITS;

    private static final long WORK_ID_MASK = (1 << WORKER_ID_BITS) - 1; // 10位workId掩码




    private long sequence;

    private long lastTime;

    private Lock lock = new ReentrantLock();
    @Autowired
    private WorkIdService workIdResolver;

    static {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, Calendar.FEBRUARY, 21);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        START_TIME_MILLIS = calendar.getTimeInMillis();
    }

    public Long generateId() {
        // time
        long currentTime;
        // seq
        long seq;

        // 此处加锁也可以使用synchronized关键字，用来在多线程并发执行时保护lastTime、sequence这两个变量
        lock.lock();
        try {
            currentTime = System.currentTimeMillis();

            //时钟被回拨，直接拒绝服务
            if (currentTime < lastTime) {
                throw new IllegalStateException("Clock go back, refused generator guid service.");
            }

            if (currentTime == lastTime) {
                //如果1ms内单台机器的4096个序号用完了，等待下一毫秒
                if (0L == (sequence = ++sequence & SEQUENCE_MASK)) {
                    lastTime = waitUntilNextMillis(currentTime);
                }
            } else {
                lastTime = currentTime;
                sequence = 0;
            }
            currentTime = lastTime;
            seq = sequence;
        }finally {
            lock.unlock();
        }

        return ((currentTime - START_TIME_MILLIS) << TIMESTAMP_LEFT_SHIFT_BITS)
                | (getWorkId() << WORKER_ID_LEFT_SHIFT_BITS) | seq;
    }


    public GuidBO parseGUID(Long id) {
        GuidBO guidBO = new GuidBO();

        //1.时间戳
        long generateTimeLong = (id >> TIMESTAMP_LEFT_SHIFT_BITS) + START_TIME_MILLIS;
        guidBO.setLockTime(new Date(generateTimeLong));

        //2.机器号
        Long workId = (id >> SEQUENCE_BITS) & WORK_ID_MASK;
        guidBO.setWorkId(workId);

        //3.机器ip
        guidBO.setWorkIpAddr(workId+"");

        //4.序列号
        guidBO.setSequence(id & SEQUENCE_MASK);

        return guidBO;
    }

    private long waitUntilNextMillis(final long fromMills) {
        long nextMills = System.currentTimeMillis();
        while (nextMills <= fromMills) {
            nextMills = System.currentTimeMillis();
        }
        return nextMills;
    }
    protected Long getWorkId() {
        return workIdResolver.generateWorkId();
    }

}
