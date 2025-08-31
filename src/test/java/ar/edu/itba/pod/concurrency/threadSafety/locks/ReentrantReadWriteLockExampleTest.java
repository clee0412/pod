package ar.edu.itba.pod.concurrency.threadSafety.locks;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

public class ReentrantReadWriteLockExampleTest {
	private final ExecutorService NEW_FIXED_THREAD_POOL = Executors.newFixedThreadPool(3);

	private final ExecutorCompletionService<String> executor = new ExecutorCompletionService<String>(
			NEW_FIXED_THREAD_POOL);

	@Test
	public void test() throws InterruptedException, ExecutionException, TimeoutException {
		executor.submit(new ReentrantReadWriteLockExample.Read(), "read done");
		executor.submit(new ReentrantReadWriteLockExample.WriteA(), "write done");
		executor.submit(new ReentrantReadWriteLockExample.WriteB(), "write done");

		for (int i = 0; i < 3; i++) {
			final Future<String> future = executor.take();
			System.out.println(future.get(1, TimeUnit.SECONDS));
		}
		NEW_FIXED_THREAD_POOL.shutdownNow();
		NEW_FIXED_THREAD_POOL.awaitTermination(2, TimeUnit.SECONDS);
	}
}
