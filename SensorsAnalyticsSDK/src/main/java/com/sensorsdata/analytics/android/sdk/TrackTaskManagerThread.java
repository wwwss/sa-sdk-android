package com.sensorsdata.analytics.android.sdk;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 王灼洲 on 2018/7/7
 */
public class TrackTaskManagerThread implements Runnable {
    private TrackTaskManager mTrackTaskManager;
    /**
     * 创建一个可重用固定线程数的线程池
     */
    private ExecutorService mPool;
    /**
     * 创建一个可重用固定线程数的线程池
     */
    private final int POOL_SIZE = 1;
    /**
     * 轮询时间，单位：毫秒
     */
    private final int SLEEP_TIME = 300;
    /**
     * 是否停止
     */
    private boolean isStop = false;

    public TrackTaskManagerThread() {
        try {
            this.mTrackTaskManager = TrackTaskManager.getInstance();
            mPool = Executors.newFixedThreadPool(POOL_SIZE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (!isStop) {
                Runnable downloadTask = mTrackTaskManager.getTrackEventTask();
                if (downloadTask != null) {
                    mPool.execute(downloadTask);
                } else {//如果当前任务队列中没有下载任务downloadTask
                    try {
                        // 查询任务完成失败的,重新加载任务队列
                        // 轮询
                        Thread.sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (isStop) {
                Runnable downloadTask = mTrackTaskManager.getTrackEventTask();
                while (downloadTask != null) {
                    mPool.execute(downloadTask);
                    downloadTask = mTrackTaskManager.getTrackEventTask();
                }
                mPool.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStop(boolean isStop) {
        this.isStop = isStop;
    }

}
