package ar.edu.itba.pod.concurrency.threadSafety.stack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StackTest {
    private static final int ELEMENTS = 5;
    private static final int THREAD_COUNT = 1000;
    private Stack stack;

    @BeforeEach
    public final void before() {
            stack = new Stack();
    }

    private final Runnable pushToStack = () -> {
        for (int i =0; i < THREAD_COUNT; i++) {
            stack.push(i);
            stack.pop();
        }
    };

    @Test
    public final void testStackEmptyException() throws InterruptedException {
        List<Thread> threads = new ArrayList<>(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            Thread thread = new Thread(pushToStack);
            threads.add(thread);
            thread.start();
            stack.pop();
        }

        for (int i = 0; i < THREAD_COUNT; i++) {
            Thread thread = threads.get(i);
            thread.join();
        }
//        assertEquals(0, stack.pop());
    }

    @Test
    public final void testArrayIndexOutOfBounds() throws InterruptedException {
        CountDownLatch startSignal = new CountDownLatch(1);
        List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

        Thread[] threads = new Thread[50];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                try {
                    startSignal.await(); // Sincronizar inicio
                    for (int j = 0; j < 100; j++) {
                        stack.push(j);
                        stack.pop();
                    }
                } catch (Exception e) {
                    exceptions.add(e);
                }
            });
            threads[i].start();
        }

        startSignal.countDown(); // Liberar todos los threads

        for (Thread t : threads) {
            t.join();
        }

        // Verificar que se generaron excepciones
        assertTrue(exceptions.stream().anyMatch(e ->
                e instanceof ArrayIndexOutOfBoundsException));
    }
}
