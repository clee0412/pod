package ar.edu.itba.pod.concurrency.iii.inmutable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadLocalRandom;

public class Subscriber {
    private final Integer id;
    private final String fullName;
    private final Date dateOfBirth;
    private final List<Subscription> subscriptions;

    public Subscriber(Integer id, String fullName, Date dateOfBirth,  List<Subscription> subscriptions) {
        this.id = id;
        this.fullName = fullName;
        this.dateOfBirth = new Date(dateOfBirth.getTime());
        this.subscriptions = new ArrayList<>(subscriptions);
    }

    public Integer getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public Date getDateOfBirth() {
        return new Date(dateOfBirth.getTime());
    }

    public List<Subscription> getSubscriptions() {
        return new ArrayList<>(subscriptions);
    }


}
