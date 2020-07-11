package ru.vladrus13.base;

public class SpecialException extends Exception{
    public SpecialException(String s) {super("Throwing special exception:\n" + s);}
}
