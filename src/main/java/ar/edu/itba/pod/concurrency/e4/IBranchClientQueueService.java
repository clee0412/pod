package ar.edu.itba.pod.concurrency.e4;

public interface IBranchClientQueueService {
    void receiveClient(Client client);
    Client clientForPriority(ClientPriority priority);
}
