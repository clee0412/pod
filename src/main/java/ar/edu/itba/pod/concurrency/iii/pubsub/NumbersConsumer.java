package ar.edu.itba.pod.concurrency.iii.pubsub;

import java.util.concurrent.BlockingQueue;

public class NumbersConsumer implements Runnable {
    private BlockingQueue<Integer> queue;
    private final int poisonPill;

    public NumbersConsumer(BlockingQueue<Integer> queue, int poisonPill) {
        this.queue = queue;
        this.poisonPill = poisonPill;
    }

    public void run() {
        try {
            int sum = 0;
            int count = 0;
            while (true) {
                Integer number = queue.take();
                if (number.equals(poisonPill)) {
                    System.out.println(Thread.currentThread().getName() +
                            " - Numbers processed: " + count + ", SUM: " + sum);
                    return;
                } else {
                    sum += number;
                    count++;
                }
                System.out.println(Thread.currentThread().getName() + " result: " + number);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}