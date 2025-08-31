package ar.edu.itba.pod.concurrency.service;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

/**
 * Basic implementation of {@link GenericService}.
 */
public class GenericServiceImpl implements GenericService {
    private int visitCount;
    private final Queue<String> queue;
    private final Object lock = new Object();
    private final Object queueLock = new Object();

    public GenericServiceImpl() {
        queue = new LinkedList<>();
        visitCount = 0;
    }

    @Override
    public String echo(String message) {
        return message;
    }

    @Override
    public String toUpper(String message) {
        Optional<String> optional = Optional.ofNullable(message);
        Optional<String> s = optional.map(m -> m.toUpperCase());

        return s.orElse(null);
    }

    @Override
    public void addVisit() {
        synchronized (lock) {
            visitCount++;
        }
    }

    @Override
    public int getVisitCount() {
        synchronized (lock) {
            return visitCount;
        }
    }

    @Override
    public boolean isServiceQueueEmpty() {
        synchronized (queueLock) {
            return queue.isEmpty();
        }
    }

    @Override
    public void addToServiceQueue(String name) {
        if (name == null) {
            throw new NullPointerException("null");
        }
        synchronized (queueLock) {
            queue.add(name);
        }
    }

    @Override
    public String getFirstInServiceQueue() {
        Optional<String> name;
        synchronized (queueLock) {
            name = Optional.ofNullable(queue.poll());
        }
        return name.orElseThrow(() -> new IllegalStateException("No one in queue"));
    }
}