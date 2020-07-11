package ru.vladrus13;

import ru.vladrus13.base.Configs;
import ru.vladrus13.base.SpecialException;
import ru.vladrus13.generator.Generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class RunRandomTests {
    public static void main(String[] args) {
        System.out.println("Starting system...");
        Configs configs;
        try {
            configs = new Configs();
        } catch (FileNotFoundException e) {
            System.out.println("File config is not exist");
            return;
        } catch (SpecialException e) {
            e.printStackTrace();
            return;
        }
        int COUNT_THREADS;
        try {
            COUNT_THREADS = Integer.parseInt(configs.get("COUNT_THREADS"));
        } catch (SpecialException e) {
            System.out.println(e.getMessage());
            return;
        }
        Set<Integer> freeNumbers = new HashSet<>();
        IntStream.range(0, COUNT_THREADS).forEach(freeNumbers::add);
        AtomicBoolean isWrongFounded = new AtomicBoolean(false);
        ExecutorService workers = Executors.newFixedThreadPool(COUNT_THREADS);
        while (!isWrongFounded.get()) {
            if (!freeNumbers.isEmpty()) {
                int number = freeNumbers.iterator().next();
                freeNumbers.remove(number);
                workers.submit(() -> {
                    boolean isFall = runTest(number, configs);
                    if (isFall) {
                        isWrongFounded.set(true);
                    }
                    freeNumbers.add(number);
                });
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean runTest(int testNumber, Configs configs) {
        String test = Generator.makeTest();
        ProcessBuilder processBuilder = new ProcessBuilder();
        try {
            BufferedWriter in = Files.newBufferedWriter(Paths.get(configs.get("MAIN_PATH") + "random_tests/" + testNumber + ".in"), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            in.write(test);
            in.close();
            boolean runFast = runProgram(testNumber, "runfast", "T", configs);
            boolean runReal = runProgram(testNumber, "run", "", configs);
            if (!runFast || !runReal) {
                return false;
            }
            processBuilder.command("diff", "-B", "tests/" + test + ".out", "random_tests/" + test + "T.out").
                    redirectOutput(new File(configs.get("MAIN_PATH") + "random_tests/" + test + ".diff"));
            Process process = processBuilder.start();
            process.waitFor();
            return Files.size(Path.of(configs.get("MAIN_PATH") + "random_tests/" + test + ".diff")) != 0;
        } catch (SpecialException | IOException | InterruptedException e) {
            System.out.println("Some bad things:\n" + e.getMessage());
            return true;
        }
    }

    private static boolean runProgram(int testNumber, String makeCommand, String namePostfix, Configs configs) throws SpecialException, IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(configs.get("MAIN_PATH"))).command("make", "--silent", makeCommand).
                redirectInput(new File(configs.get("MAIN_PATH") + "random_tests/" + testNumber + ".in")).
                redirectOutput(new File(configs.get("MAIN_PATH") + "random_tests/" + testNumber + namePostfix + ".out"));
        Process process = processBuilder.start();
        process.waitFor();
        int exitCode = process.exitValue();
        return exitCode == 0;
    }
}
