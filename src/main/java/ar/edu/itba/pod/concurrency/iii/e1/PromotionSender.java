package ar.edu.itba.pod.concurrency.iii.e1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PromotionSender {
    public static void main(String[] args) {
        List<String> promotions = Arrays.asList(
                "Descuento en Cafe: ",
                "Descuento en Refrescos: ",
                "Descuento en Congelados: "
        );
//        notifyPromotions(promotions);
        notifyPromotionsCompletable(promotions);
        System.out.println("Se realizaron todas las notificaciones de la promocion.");
    }

    private static void notifyCustomers(String promotion) {
        try {
            TimeUnit.SECONDS.sleep(1);
            System.out.println("Cliente: " + promotion);
        } catch (InterruptedException e) {
            //
        }
    }

    private static void notifyMarketing(String promotion) {
        try {
            TimeUnit.SECONDS.sleep(1);
            System.out.println("Marketing: " + promotion);
        } catch (InterruptedException e) {
            //
        }
    }

    private static void notifyPromotions(List<String> promotions) {
        for (String promotion : promotions) {
            promotion = promotion + "30%";
            promotion = promotion + " Solo por hoy";
            notifyCustomers(promotion);
        }
        notifyMarketing("Hoy se publicito un descuento del 30%");

    }

    private static void notifyPromotionsParallel(List<String> promotions) {
        promotions.parallelStream().forEach(promotion -> {
            promotion = promotion + "30%";
            promotion = promotion + " Solo por hoy";
            notifyCustomers(promotion);
        });
        notifyMarketing("Hoy se publicito un descuento del 30%");
    }

    private static void notifyPromotionsCompletable(List<String> promotions) {
        ExecutorService service = Executors.newCachedThreadPool();

       List<CompletableFuture<Void>> futurePromotions = promotions.stream().map(p -> CompletableFuture.runAsync(() -> notifyCustomers(p + "30% Solo por hoy"), service)).collect(Collectors.toList());

        futurePromotions.add(CompletableFuture.runAsync(() -> notifyMarketing("Hoy se publicito un descuento del 30%"), service));

        CompletableFuture.allOf(futurePromotions.toArray(new CompletableFuture[0])).join();
    }
}
