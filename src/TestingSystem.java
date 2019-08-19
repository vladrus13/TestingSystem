import base.Configs;
import base.SpecialException;
import base.Tests;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class TestingSystem {
    public static void main(String[] args) throws SpecialException, IOException, InterruptedException {
        System.out.println("Starting system...");
        System.out.println("Found the tests...");
        Configs configs = null;
        Tests tests = null;
        try {
            configs = new Configs();
            tests = new Tests(configs);
        } catch (FileNotFoundException e) {
            System.out.println("File config is not exist");
            System.exit(0);
        } catch (SpecialException e) {
            e.printStackTrace();
        }
        System.out.println("Make a result file...");
        FileWriter out = null;
        try {
            out = new FileWriter(configs.get("MAIN_PATH") + "result.txt", false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File testOutput = null, outTest = null;
        try {
            testOutput = new File(configs.get("MAIN_PATH") + configs.get("FILE_NAME") + ".in");
            outTest = new File(configs.get("MAIN_PATH") + "output.outout");
        } catch (SpecialException e) {
            e.printStackTrace();
        }
        long startTest;
        System.out.println("Compile Program...");
        ProcessBuilder builderComplile = new ProcessBuilder();
        builderComplile.command("sh", "compile");
        builderComplile.directory(new File(configs.get("MAIN_PATH")));
        startTest = System.currentTimeMillis();
        Process process = builderComplile.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            errorCode(new BufferedReader(new InputStreamReader(process.getErrorStream())));
        }
        out.write("Compile time: " + (System.currentTimeMillis() - startTest) + " ms.\n");
        System.out.println("Launch tests...");
        int accepted = 0, wrong = 0;
        for (int i = 1; i <= Integer.parseInt(configs.get("COUNT_OF_TESTS")); i++) {
            System.out.println("Testing test " + i + "...");
            File templateFile = tests.getInTest(i);
            File templateFileout=  tests.getOutTest(i);
            try {
                move(templateFile, testOutput);
                move(templateFileout, outTest);
            } catch (IOException e) {
                e.printStackTrace();
            }
            startTest = System.currentTimeMillis();
            builderComplile.command("sh", "run");
            process = builderComplile.start();
            exitCode = process.waitFor();
            if (exitCode != 0) {
                errorCode(new BufferedReader(new InputStreamReader(process.getErrorStream())));
            }
            builderComplile.command("sh", "diff");
            process = builderComplile.start();
            exitCode = process.waitFor();
            if (exitCode == 0) {
                out.write("Test " + i + " accepted! Time: " + (System.currentTimeMillis() - startTest) + "ms.\n");
                accepted++;
            } else {
                out.write("Test " + i + ". Wrong answer! Time: " + (System.currentTimeMillis() - startTest) + "ms.\n");
                wrong++;
            }
        }
        out.write("================================\n");
        out.write(String.format("Total: %d, accepted: %d, wrong answer: %d", accepted + wrong, accepted, wrong));
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void errorCode(BufferedReader error) throws IOException {
        String errorString = bufferCout(error);
        System.out.println("Compilation Error" + "\n" + errorString);
        System.exit(0);
    }

    private static String bufferCout(BufferedReader cout) throws IOException {
        String ret = "";
        String template = "";
        while ((template = cout.readLine()) != null) {
            ret += template + '\n';
        }
        return ret;
    }

    private static void move(File a, File b) throws IOException {
        Files.copy(a.toPath(), b.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

}
