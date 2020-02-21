import joptsimple.*;

public class Main {

    //program arguments are set in configurations(if you are using intellij idea)
    public static void main(String[] args) {
        CommandLineParser myParser = new CommandLineParser(args);
        System.out.println(myParser.getA());
        System.out.println(myParser.getB());
        System.out.println(myParser.getC());
    }
}
