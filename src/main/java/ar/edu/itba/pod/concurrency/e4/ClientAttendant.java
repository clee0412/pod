package ar.edu.itba.pod.concurrency.e4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.Callable;

public class ClientAttendant implements Callable<Integer> {
    private final IBranchClientQueueService clientService;
    private final ClientPriority priority;
    private final Random random = new Random();
    private final String attendantName;
    private static final Logger logger = LoggerFactory.getLogger(ClientAttendant.class);

    public ClientAttendant(IBranchClientQueueService clientService, ClientPriority priority, String attendantName) {
        this.clientService = clientService;
        this.priority = priority;
        this.attendantName = attendantName;
    }

    @Override
    public Integer call() throws Exception {
        boolean stillWorking = true;
        Integer numberOfClients = 0;
        int attempts = 0;
        while (stillWorking) {
            Client client = clientService.clientForPriority(priority);

            if (client != null) {
                logger.info("[{}] attending client {}", attendantName, client.name());
                numberOfClients++;
                attempts =0;
                Thread.sleep(random.nextInt(5000) + 3000);
            } else {
                attempts++;
                if (attempts == 3) {
                    logger.info("{} waited enough! Leaving byee", attendantName);
                    stillWorking = false;
                } else {
                    logger.info("[{}] received no client... just waiting", attendantName);
                    Thread.sleep(random.nextInt(5000) + 3000);
                }
            }



            // if 3 cycles with no client -> end.
            // get one client and sleep for random amount of seconds to simulate service time
            // or if no client sleep to simulate waiting time
        }
        return numberOfClients; // how mnay clients
    }
}
