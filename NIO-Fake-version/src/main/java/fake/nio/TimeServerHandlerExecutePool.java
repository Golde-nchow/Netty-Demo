package fake.nio;

import java.util.concurrent.*;

/**
 * @author by chow
 * @Description 时间服务器处理器线程池
 * @date 2021/1/25 下午11:18
 */
public class TimeServerHandlerExecutePool {

    private ExecutorService executor;

    public TimeServerHandlerExecutePool(int maxPoolSize, int queueSize) {
        // 推荐使用可命名的线程池工厂，这样就可以为线程设置名字啦！！！
        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        // 核心线程池大小、最大线程池大小、线程空闲时间、线程空闲时间单位、工作队列
        executor = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                maxPoolSize,
                120L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(queueSize),
                threadFactory
        );
    }

    public void execute(Runnable task) {
        executor.execute(task);
    }
}
