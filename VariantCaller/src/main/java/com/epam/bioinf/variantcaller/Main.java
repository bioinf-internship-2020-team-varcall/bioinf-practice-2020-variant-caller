package com.epam.bioinf.variantcaller;

public class Main {
    //this program is temporary and will be replaced in future versions
    public static void main(String[] args) {
        //program arguments are set in configurations(if you are using intelliJ IDEA)
        try {
            CommandLineParser myParser = new CommandLineParser(args);
            System.out.println(myParser.getA());
        } catch (Exception e) {
            System.err.println("Programs arguments are invalid, please use only -a arguments");
        }
    }
}
