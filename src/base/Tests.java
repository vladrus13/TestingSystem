package base;

import java.io.File;

public class Tests {
    private static File test;

    public Tests(Configs configs) throws SpecialException {
        test = new File(configs.get("MAIN_PATH") + "tests");
    }

    public static File getInTest(int number) {
        return new File(test, Integer.toString(number) + ".in");
    }

    public static File getOutTest(int number) {
        return new File(test, Integer.toString(number) + ".out");
    }
}
