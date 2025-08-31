package ar.edu.itba.pod.concurrency.iii.e4;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.concurrent.*;


public class ScheduledTest {

    public class TimestampTask implements Runnable {
        private final String name;

        public TimestampTask(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            System.out.println(name + " time: " + new Date(currentTime));
        }
    }

    @Test
    public final void test() throws InterruptedException, ExecutionException {
        final ScheduledExecutorService executorService = Executors
                .newSingleThreadScheduledExecutor();
//        final ScheduledExecutorService executorService = Executors
//                .newScheduledThreadPool(2);
        try {
            // Tarea 1: Se ejecuta cada 10 segundos
            ScheduledFuture<?> repeatingTask = executorService.scheduleAtFixedRate(
                    new TimestampTask("R"), 1, 2, TimeUnit.SECONDS);


            // Tarea 2: Se ejecuta una vez después de 60 segundos y cancela la primera
            executorService.schedule(() -> {
                System.out.println("Canceling repeating task after 8 seconds");
                repeatingTask.cancel(true);
            }, 8, TimeUnit.SECONDS);
//            executorService.scheduleWithFixedDelay(new TimestampTask("D"), 0, 1,
//                    TimeUnit.SECONDS);
//            Thread.sleep(3000);
            Thread.sleep(10000);

        } finally {
            executorService.shutdown();
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        }
    }
}
