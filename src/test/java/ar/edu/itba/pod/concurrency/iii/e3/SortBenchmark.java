package ar.edu.itba.pod.concurrency.iii.e3;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Spliterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Predicate;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Benchmar to compare between {@link Arrays#parallelSort(int[])} and
 * {@link Arrays#sort(int[])}
 */
public class SortBenchmark {
//    private static final int SIZE_1 = 10_000_000;
    private static final int SIZE_1 = 1_000;
//    private static final int SIZE_2 = 25_000_000;
    private static final int SIZE_2 = 5_000;
//    private static final int SIZE_3 = 50_000_000;
    private static final int SIZE_3 = 10_000;
    private static final int ITERATIONS = 4;


    private int[] generateRandomArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = ThreadLocalRandom.current().nextInt(1_000_000);
        }
        return array;
    }

    private long measureTime(int[] array, Consumer<int[]> sortFunction) {
        int[] copy = Arrays.copyOf(array, array.length);
        long startTime = System.nanoTime();
        sortFunction.accept(copy);
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    private double calculateAverage(int[] originalArray, Consumer<int[]> sortFunction) {
        long totalTime = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            totalTime += measureTime(originalArray, sortFunction);
        }

        return totalTime / (double) ITERATIONS;
    }


    @Test
    public void benchmark_all() {
        int[] sizes = {SIZE_1, SIZE_2, SIZE_3};
        String[] sizeLabels = {"10M", "25M", "50M"};

        for (int i = 0; i < sizes.length; i++) {
            System.out.println("\n=== Array size: " + sizeLabels[i] + " elements ===");
            int[] array = generateRandomArray(sizes[i]);

            // Sequential sort
            double sequentialAvg = calculateAverage(array, Arrays::sort);

            // Parallel sort
            double parallelAvg = calculateAverage(array, Arrays::parallelSort);

            System.out.printf("Sequential sort average: %.2f ms%n", sequentialAvg / 1_000_000.0);
            System.out.printf("Parallel sort average: %.2f ms%n", parallelAvg / 1_000_000.0);
            System.out.printf("Speedup: %.2fx%n", sequentialAvg / parallelAvg);
        }

    }

}
