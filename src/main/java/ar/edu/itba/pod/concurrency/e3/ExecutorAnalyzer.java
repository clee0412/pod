package ar.edu.itba.pod.concurrency.e3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.IntStream;

public class ExecutorAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorAnalyzer.class);
    private static final int THREAD_COUNT = 4;

    private static final Function<Integer, Callable<Void>> runnerFactory = (Integer index) -> () -> {
        logger.info("Starting runner: {}", index);
        Thread.sleep(1500);
        logger.info("Ending runner: {}", index);
        return null;
    };

    public static void execute(ExecutorService pool) {
        try {
            List<Future<Void>> futures = IntStream.range(0, THREAD_COUNT).mapToObj(index -> pool.submit(runnerFactory.apply(index))).toList();

            for (Future<Void> future : futures) {
                future.get();
            }
            pool.shutdown();
            if (!pool.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException | ExecutionException e) {
            pool.shutdownNow();
        }
    }

    public static void main(String[] args) {
//        logger.info("Cached thread pool");
//        execute(Executors.newCachedThreadPool());

//        logger.info("Fixed thread pool");
//        execute(Executors.newFixedThreadPool(2));
//
//        logger.info("Single thread executor");
//        execute(Executors.newSingleThreadExecutor());
//
        logger.info("Single thread executor BUT rejecting");
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new SynchronousQueue<>(), new ThreadPoolExecutor.AbortPolicy());
        execute(executor);

    }
}
