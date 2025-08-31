package ar.edu.itba.pod.concurrency.e4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class LocalBranch {
    private static Integer AMOUNT_OF_CLIENTS = 200;
    private static Integer AMOUNT_OF_RECEPTIONISTS = 2;
    private static Integer AMOUNT_OF_ATTENDANTS_HIGH = 3;
    private static Integer AMOUNT_OF_ATTENDANTS_PRIORITY = 1;
    private static Integer AMOUNT_OF_ATTENDANTS_NORMAL = 2;

    private static final Logger logger = LoggerFactory.getLogger(LocalBranch.class);

    public static void main(String[] args) {
        IBranchClientQueueService sharedQueue = new BranchClientQueueService();
        ExecutorService receptionistPool = Executors.newFixedThreadPool(AMOUNT_OF_RECEPTIONISTS);
        ExecutorService attendantPool = Executors.newFixedThreadPool(AMOUNT_OF_ATTENDANTS_HIGH + AMOUNT_OF_ATTENDANTS_PRIORITY + AMOUNT_OF_ATTENDANTS_NORMAL);

        List<Future<Integer>> receptionistResults = new ArrayList<>();
        List<Future<Integer>> highPriorityResults = new ArrayList<>();
        List<Future<Integer>> priorityPriorityResults = new ArrayList<>();
        List<Future<Integer>> normalPriorityResults = new ArrayList<>();

        for (int i = 0; i < AMOUNT_OF_RECEPTIONISTS; i++) {
            receptionistResults.add(receptionistPool.submit(new Receptionist(sharedQueue, "R-" + i)));
        }
        // Wait for some time before starting attendants
        try {
            Thread.sleep(5000); // Wait 5 seconds
            logger.info("Starting attendants now...");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        for (int i = 0; i < AMOUNT_OF_ATTENDANTS_HIGH; i++) {
            highPriorityResults.add(attendantPool.submit(new ClientAttendant(sharedQueue, ClientPriority.HIGH, "HIGH-ATTENDANT-" + i)));
        }
        for (int i = 0; i < AMOUNT_OF_ATTENDANTS_PRIORITY; i++) {
            priorityPriorityResults.add(attendantPool.submit(new ClientAttendant(sharedQueue, ClientPriority.PRIORITY, "PRIORITY-ATTENDANT-" + i)));
        }
        for (int i = 0; i < AMOUNT_OF_ATTENDANTS_NORMAL; i++) {
            normalPriorityResults.add(attendantPool.submit(new ClientAttendant(sharedQueue, ClientPriority.NORMAL, "NORMAL-ATTENDANT-" + i)));
        }

        try {
            Map<String, Integer> results = new HashMap<>();
            int totalReceived = 0;

            for (Future<Integer> receptionistResult : receptionistResults) {
                totalReceived += receptionistResult.get();
                logger.debug("Receptionist result: {}", receptionistResult);
            }
            int highServed = 0, priorityServed = 0, normalServed = 0;

            for (Future<Integer> result : highPriorityResults) {
                highServed += result.get();
                logger.debug("High Priority result: {}", highServed);
            }
            for (Future<Integer> result : priorityPriorityResults) {
                priorityServed += result.get();
                logger.debug("Priority Priority result: {}", priorityServed);

            }
            for (Future<Integer> result : normalPriorityResults) {
                normalServed += result.get();
                logger.debug("Normal Priority result: {}", normalServed);
            }

            logger.info("\n=== SIMULATION RESULTS ===");
            logger.info("Total clients received: {}", totalReceived);
            logger.info("HIGH priority served: {}", highServed);
            logger.info("PRIORITY served: {}", priorityServed);
            logger.info("NORMAL served: {}", normalServed);
            logger.info("Total served: {}", (highServed + priorityServed + normalServed));
            logger.info("Clients remaining in queue: {}", (totalReceived - (highServed +priorityServed + normalServed)));


            receptionistPool.shutdown();
            attendantPool.shutdown();

            if (!receptionistPool.awaitTermination(30, TimeUnit.SECONDS)) {
                logger.warn("Receptionists didn't finish in time, forcing shutdown");
                receptionistPool.shutdownNow();
            }
            if (!attendantPool.awaitTermination(60, TimeUnit.MILLISECONDS)) {
                logger.warn("Attendants didn't finish in time, forcing shutdown");
                attendantPool.shutdownNow();
            }

        } catch (InterruptedException | ExecutionException e) {

            receptionistPool.shutdownNow();
            attendantPool.shutdownNow();
        }

    }
}
