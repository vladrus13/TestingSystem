import base.Configs;
import base.SpecialException;
import base.Tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class TestingSystem {
    public static void main(String[] args) {
        System.out.println("Starting system...");
        System.out.println("Found the tests...");
        Tests tests = new Tests();
        Configs configs = null;
        try {
            configs = new Configs();
        } catch (FileNotFoundException e) {
            System.out.println("File config is not exist");
            System.exit(0);
        } catch (SpecialException e) {
            e.printStackTrace();
        }
        System.out.println("Make a result file...");
        File resultFile = new File("result.txt");
        resultFile.delete();
        try {
            resultFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PrintWriter out = new PrintWriter("result.txt");
        } catch (FileNotFoundException e) {
            System.out.println("Some problem with result File");
            System.exit(0);
        }

        System.out.println("Launch tests...");
        File output = null;
        try {
            output = new File(configs.get("NAME_RESULT"));
        } catch (SpecialException e) {
            e.printStackTrace();
        }
        while (tests.isEnd()) {
            File templateFile = tests.getTest();
            tests.nextTest();
            try {
                move(templateFile, output);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }
    }

    private static void move(File a, File b) throws IOException {
        Files.copy(a.toPath(), b.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

}
