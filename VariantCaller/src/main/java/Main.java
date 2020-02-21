import joptsimple.*;

public class Main {

    public static void main(String[] args) {
        //program arguments are set in configurations(if you are using intellij idea)
        CommandLineParser myParser = new CommandLineParser(args);
        System.out.println(myParser.getA());
        System.out.println(myParser.getB());
        System.out.println(myParser.getC());
    }
}
