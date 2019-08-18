package base;

public class SpecialException extends Exception{
    public SpecialException(String s) {super("throwing special exception:\n" + s);}
}
