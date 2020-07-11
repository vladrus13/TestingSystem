import base.Configs;
import base.HTMLWriter;
import base.SpecialException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestingSystem {

    static final int COUNT_THREADS = 100;

    public static void main(String[] args) throws SpecialException, IOException, InterruptedException {
        System.out.println("Starting system...");
        System.out.println("Found the tests...");
        Configs configs = null;
        try {
            configs = new Configs();
        } catch (FileNotFoundException e) {
            System.out.println("File config is not exist");
            System.exit(0);
        } catch (SpecialException e) {
            e.printStackTrace();
            System.exit(0);
        }
        int countTest = Integer.parseInt(configs.get("COUNT_OF_TESTS"));
        HTMLWriter htmlWriter = new HTMLWriter(countTest);
        System.out.println("Make a result file...");
        long startTest;
        long timeLimit = Integer.parseInt(configs.get("TIME_LIMIT"));
        System.out.println("Compile Program...");
        ProcessBuilder builderComplile = new ProcessBuilder();
        builderComplile.directory(new File(configs.get("MAIN_PATH"))).command("make", "--silent", "all");
        startTest = System.currentTimeMillis();
        Process process = builderComplile.start();
        int exitCode = process.waitFor();
        htmlWriter.setCompileTime(System.currentTimeMillis() - startTest);
        if (exitCode != 0) {
            htmlWriter.setCompilation(bufferCout(new BufferedReader(new InputStreamReader(process.getErrorStream()))));
            htmlWriter.writeHTML();
            return;
        }
        ExecutorService workers = Executors.newFixedThreadPool(COUNT_THREADS);
        for (int i = 1; i <= countTest; i++) {
            int finalI = i;
            Configs finalConfigs = configs;
            workers.submit(() -> runTest(htmlWriter, finalI, finalConfigs, timeLimit));
        }
        workers.shutdown();
        workers.awaitTermination(2 * countTest * timeLimit, TimeUnit.MILLISECONDS);
        htmlWriter.writeHTML();
    }

    private static synchronized void runTest(HTMLWriter htmlWriter, int test, Configs configs, long timeLimit) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        Process process;
        System.out.println("Run " + test);
        try {
            File openFile = new File(configs.get("MAIN_PATH") + "tests/" + test + ".in");
            if (!openFile.exists()) {
                System.out.println("Test " + test + " doesn't exist!");
                return;
            }
            processBuilder.directory(new File(configs.get("MAIN_PATH"))).command("make", "--silent", "run").
                    redirectInput(new File(configs.get("MAIN_PATH") + "tests/" + test + ".in")).
                    redirectOutput(new File(configs.get("MAIN_PATH") + "tests/" + test + "T.out"));
            long startTime = System.currentTimeMillis();
            process = processBuilder.start();
            process.waitFor(timeLimit * 3 / 2, TimeUnit.MILLISECONDS);
            long endTest = System.currentTimeMillis();
            if (endTest - startTime > timeLimit) {
                htmlWriter.setTimeLimit(test);
                return;
            }
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                htmlWriter.setRuntimeError(test, exitCode, endTest - startTime);
                return;
            }
            processBuilder.command("diff", "-B", "tests/" + test + ".out", "tests/" + test + "T.out").
                    redirectOutput(new File(configs.get("MAIN_PATH") + "tests/" + test + ".diff"));
            process = processBuilder.start();
            process.waitFor();
            if (Files.size(Path.of(configs.get("MAIN_PATH") + "tests/" + test + ".diff")) != 0) {
                htmlWriter.setWrongAnswer(test, endTest - startTime);
            } else {
                htmlWriter.setAccepted(test, endTest - startTime);
            }
        } catch (SpecialException | IOException | InterruptedException exception) {
            System.out.println("Got smth unreal\n" + exception.toString());
        }
    }

    private static String bufferCout(BufferedReader cout) throws IOException {
        StringBuilder ret = new StringBuilder();
        String template;
        while ((template = cout.readLine()) != null) {
            ret.append(template).append('\n');
        }
        return ret.toString();
    }
}
