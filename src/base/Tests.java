package base;

import java.io.File;

public class Tests {
    private static int counter = 0;
    private static File[] testsFiles;
    public Tests() {
        counter = 0;
        File template = new File("tests");
        testsFiles = template.listRoots();
    }

    public boolean isEnd() {
        return counter >= testsFiles.length - 1;
    }

    public void nextTest() {
        counter++;
        if (counter == testsFiles.length) {
            counter--;
        }
    }

    public File getTest() {
        return testsFiles[counter];
    }

}
