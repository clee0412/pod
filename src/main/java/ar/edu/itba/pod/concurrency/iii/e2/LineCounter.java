package ar.edu.itba.pod.concurrency.iii.e2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LineCounter {
    private static final String PATH = "/var/log/";

    private static Long countLines(Path path) {
        if (Files.isDirectory(path)) {
            return 0L;
        }
        try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
            return lines.count();
        } catch (IOException e) {
            System.out.println("Error counting lines " + e.getMessage());
            return 0L;
        }
    }

    private static Long getFileSize(Path path) {
        if (Files.isDirectory(path)) {
            return 0L;
        }
        try {
            return Files.size(path);
        } catch (IOException e) {
            System.out.println("Error getting file size " + e.getMessage());
            return 0L;
        }
    }

    public static CompletableFuture<String> processFile(Path file, ExecutorService service) {
        CompletableFuture<Long> lineCounterFuture = CompletableFuture.supplyAsync(() -> countLines(file), service);
        CompletableFuture<Long> fileSizeFuture = CompletableFuture.supplyAsync(() -> getFileSize(file), service);

        CompletableFuture<String> combined = lineCounterFuture.thenCombineAsync(fileSizeFuture, (l, f) -> file.toString() + ", Lines: " + l + ", Size: " + f + " bytes\n", service);

//        combined.thenAccept(buffer::append);
        combined.thenAccept(System.out::println);
        return combined;
        // how to make it not return yet
    }

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        Path path = Paths.get(PATH);
        List<Path> files = listFiles(path);
        ExecutorService service = Executors.newCachedThreadPool();
        StringBuffer buffer = new StringBuffer();
        List<CompletableFuture<String>> futures = new ArrayList<>();

        for (Path file : files) {
            CompletableFuture<String> futureString = processFile(file, service);
            futures.add(futureString);
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])); // esperar a que TODOS terminen
        // cuando todos terminen, llenar el buffer??
        allFutures.thenAccept(result -> {
            for (CompletableFuture<String> future : futures) {
                try {
                    buffer.append(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println("Error processing file " + e.getMessage());
                }
            }
        }).join(); // esperar a que todo termine


        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);
    }

    private static List<Path> listFiles(Path dir) throws IOException {
        DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
        List<Path> files = new ArrayList<>();
        for (Path path : stream) {
            files.add(path);
        }
        return files;
    }

}
