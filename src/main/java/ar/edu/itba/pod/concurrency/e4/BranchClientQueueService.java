package ar.edu.itba.pod.concurrency.e4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BranchClientQueueService implements IBranchClientQueueService {
    private final Map<ClientPriority, Queue<Client>> queues = Map.of(
            ClientPriority.HIGH, new ConcurrentLinkedQueue<>(),
            ClientPriority.PRIORITY, new ConcurrentLinkedQueue<>(),
            ClientPriority.NORMAL, new ConcurrentLinkedQueue<>()
    );

    private final Map<ClientPriority, Object> locks = Map.of(
            ClientPriority.HIGH, new Object(),
            ClientPriority.PRIORITY, new Object(),
            ClientPriority.NORMAL, new Object()
    );
    private static final Logger logger = LoggerFactory.getLogger(BranchClientQueueService.class);

    @Override
    public void receiveClient(Client client) {
        logger.info("Receptionist talking to client {}", client.name());
        Queue<Client> queue = queues.get(client.priority());
        synchronized (locks.get(client.priority())) {
            queue.add(client);
        }
        // talk to client --> sleep
        // enqueue in a shared queue that will be used by attendants
    }

    @Override
    public Client clientForPriority(ClientPriority priority) {
        Queue<Client> queue = queues.get(priority);
//        Client client = null;
        synchronized (locks.get(priority)) {
            return queue.poll();
        }
//        if (client == null) {
//            logger.info("No client found with priority {}", priority.name());
//        }
//        return client;
    }
}
