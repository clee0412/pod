package ar.edu.itba.pod.concurrency.threadSafety.stack;

public class Stack {
    private static final int MAX_SIZE = 10;
    private int top = 0;
    private final int[] values = new int[MAX_SIZE];

    public synchronized void push(final int n) {
            if (top == MAX_SIZE) { // variable critica
                throw new IllegalStateException("Stack is full");
            }
            values[top++] = n; // aca puede haber un error porq esta leyendo y aumentando y guarando
    }

    public synchronized int pop() {
            if (top == 0) { // variable critica
                throw new IllegalStateException("Stack is empty");
            }
            return values[--top]; // lo mismo aca
    }

}
