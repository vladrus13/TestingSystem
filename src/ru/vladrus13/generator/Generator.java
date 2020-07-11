package ru.vladrus13.generator;

import java.util.Random;

public class Generator {
    private static final Random random = new Random();

    public static String makeTest() {
        int a, b;
        a = random.nextInt(1000);
        b = random.nextInt(10000);
        return a + " " + b;
    }
}