package ar.edu.itba.pod.concurrency.threadSafety.e5;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class LineCounter {
    private static final String PATH = "/var/log/";

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        Path path = Paths.get(PATH);
        List<Path> files = listFiles(path);

        ExecutorService executor = Executors.newCachedThreadPool();
        List<Callable<Long>> calls = new ArrayList<>();
        for (Path file : files) {
            calls.add(new FileLinesCounter(file));
        }
        List<Future<Long>> futures = executor.invokeAll(calls);
        Long count = 0L;
        for (Future<Long> future : futures) {
            count += future.get();
        }
        System.out.println(count);
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

    private static List<Path> listFiles(Path dir) throws IOException {
        List<Path> result = new ArrayList<>();
        DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
        for (Path entry : stream) {
            result.add(entry);
        }
        return result;
    }

    public static class FileLinesCounter implements Callable<Long> {
        private Path path;
        public FileLinesCounter(Path path) {
            this.path = path;
        }
        @Override
        public Long call() throws Exception {
            if (path.toFile().isFile()) {
                return Files.lines(path, StandardCharsets.ISO_8859_1).count();
            }
            return 0L;
        }
    }

}
