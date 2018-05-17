package main;

import java.util.ArrayList;
import java.util.List;
import rde.utils.Utils;

/**
 *
 * @author vitor
 */
public class UnitTest {

    public static void main(String[] args) {
        csvExtractor(args);
        programExtractor(args);
    }

    private static String[] toArray(List<String> args) {
        String[] result = new String[args.size()];
        int index = 0;
        for (String a : args) {
            result[index] = a;
            index++;
        }
        return result;
    }

    private static void csvExtractor(String[] args) {
        Utils.print(0, "----------------------------");
        Utils.print(0, "[Extraction] CSV Cartridge");

//        extraction
        List<String> arguments = new ArrayList<>();
        arguments.add("CSV:EXTRACT");
        arguments.add("de_csv");
        arguments.add("/Users/vitor/Documents/Repository/Thesis/DataflowAnalyzer/RawDataExtractor");
        arguments.add("test*");
        arguments.add("[A:numeric,B:numeric]");
        arguments.add("-delimiter=\";\"");
        args = toArray(arguments);
        RawDataExtractor.run(args);
        
//        access
        arguments = new ArrayList<>();
        arguments.add("CSV:ACCESS");
        arguments.add("de_csv");
        arguments.add("/Users/vitor/Documents/Repository/Thesis/DataflowAnalyzer/RawDataExtractor");
        arguments.add("de_csv.data");
        args = toArray(arguments);
        RawDataExtractor.run(args);
    }

    private static void programExtractor(String[] args) {
        Utils.print(0, "----------------------------");
        Utils.print(0, "[Extraction] External Program");
        
//        extraction
        List<String> arguments = new ArrayList<>();
        arguments.add("PROGRAM:EXTRACT");
        arguments.add("de_program");
        arguments.add("/Users/vitor/Documents/Repository/Thesis/DataflowAnalyzer/RawDataExtractor");
        arguments.add("./program.sh");
        arguments.add("[A:numeric:key,B:numeric]");
        args = toArray(arguments);
        RawDataExtractor.run(args);

//        access
        arguments = new ArrayList<>();
        arguments.add("PROGRAM:ACCESS");
        arguments.add("de_program");
        arguments.add("/Users/vitor/Documents/Repository/Thesis/DataflowAnalyzer/RawDataExtractor");
        arguments.add("de_program.data");
        args = toArray(arguments);
        RawDataExtractor.run(args);
    }
}
