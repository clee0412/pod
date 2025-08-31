package ar.edu.itba.pod.concurrency.e4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.Callable;

public class Receptionist implements Callable<Integer> {
    private static final Integer AMOUNT_OF_CLIENTS = 100;
    private final IBranchClientQueueService clientService;
    private final String receptionistName;
    private static final Logger logger = LoggerFactory.getLogger(Receptionist.class);
    private final Random random = new Random();


    public Receptionist(IBranchClientQueueService clientService, String receptionistName) {
        this.clientService = clientService;
        this.receptionistName = receptionistName;
    }

    @Override
    public Integer call() throws Exception {
        ClientPriority[] priorities = ClientPriority.values();

        for (int i = 0; i < AMOUNT_OF_CLIENTS; i++) {
//            logger.info("[{}] with client #{}", receptionistName, i);
            ClientPriority randomPriority = priorities[random.nextInt(priorities.length)];
            clientService.receiveClient(new Client("C" + i + receptionistName, randomPriority));
            Thread.sleep(1500);
            // simulate one client and enqueue
            // sleep for a couple of random seconds
        }
        return AMOUNT_OF_CLIENTS;
    }
}
