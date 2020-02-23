package com.epam.bioinf.variantcaller;

public class Main {

    public static void main(String[] args) {
        //program arguments are set in configurations(if you are using intelliJ IDEA)
        CommandLineParser myParser = new CommandLineParser(args);
        System.out.println(myParser.getA());
    }
}
