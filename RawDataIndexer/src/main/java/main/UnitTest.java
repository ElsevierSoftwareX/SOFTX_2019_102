package main;

import indexer.PostgresRAWIndexer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rdi.utils.Utils;

/**
 *
 * @author vitor
 */
public class UnitTest {

    static String path = "/home/vitor/Documents/dev/dataflowanalyzer/RawDataIndexer/";
    static String fastBitBin = "/home/vitor/Documents/program/fastbit-2.0.3/bin/";
    static String postgresRAWBin = "/home/vitor/Softwares/PostgresRAW/bin";
    static String postgresRAWpgDataDir = "/home/thaylon/pgData";

    public static void main(String[] args) {
        try {
            Utils.runCommand("rm -rf indexes*;rm -rf di_*;rm -rf FastBit_*; rm -rf OptimizedFastBit_*", path, false);
        } catch (IOException ex) {
            Logger.getLogger(UnitTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(UnitTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        postgresRAWIndexer();

        csvIndexer(args);
        
        List<String> testOptions = new ArrayList<>();
        testOptions.add("");
        testOptions.add("-option=[e:range]");
        testOptions.add("-option=[e:interval-equality]");
        testOptions.add("-option=[b:precision=2,e:interval-equality]");
        
        List<String> testIndexerNames = new ArrayList<>();
        testIndexerNames.add("default");
        testIndexerNames.add("range");
        testIndexerNames.add("interval");
        testIndexerNames.add("interval-with-precision");
        
        String[] indexFolderNameSufix = {
            "di_%sfb_idx-default-0_b.precision=2_e.equality",
            "di_%sfb_idx-range-1_b.precision=2_e.range",           
            "di_%sfb_idx-interval-2_b.precision=2_e.interval-equality",
            "di_%sfb_idx-interval-with-precision-3_b.precision=2_e.interval-equality"            
        };
                
        int idx = 0;
        for (int i=0; i<testOptions.size(); i++){
            String opt = testOptions.get(i);
            String indexerName = testIndexerNames.get(i);
            fastbitIndexer(args, opt, idx, indexerName, String.format(indexFolderNameSufix[i], ""));
            optimizedFastBitIndexer(args, opt, idx, indexerName, String.format(indexFolderNameSufix[i], "o"));
            idx++;
        }                
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

    private static void csvIndexer(String[] args) {
        Utils.print(0, "----------------------------");
        Utils.print(0, "[Indexing] CSV Cartridge");

//        extraction
        List<String> arguments = new ArrayList<>();
        arguments.add("CSV:INDEX");
        arguments.add("di_csv");
        arguments.add(path);
        arguments.add("test*.csv");
        arguments.add("[A:numeric,B:numeric,C:numeric]");
        arguments.add("-delimiter=\",\"");
        args = toArray(arguments);
        RDI.run(args);

//        access
        arguments = new ArrayList<>();
        arguments.add("CSV:ACCESS");
        arguments.add("di_csv");
        arguments.add(path);
        arguments.add("test1.csv");
        arguments.add("[A:16,B:18,C:22]");
        arguments.add("-delimiter=\",\"");
//        arguments.add("-verbose");
        args = toArray(arguments);
        RDI.run(args);
    }

    private static void fastbitIndexer(String[] args, String opt, int idx, String indexerName, String indexFolderName) {
        try {
            Utils.runCommand("rm -rf indexes*", path, false);
        } catch (IOException ex) {
            Logger.getLogger(UnitTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(UnitTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Utils.print(0, "----------------------------");
        Utils.print(0, "[Indexing] FastBit Cartridge (" + indexerName + ")");

//        extraction
        List<String> arguments = new ArrayList<>();
        arguments.add("FASTBIT:INDEX");
        arguments.add("di_fb_idx-" + indexerName + "-" + idx);
        arguments.add(path);
        arguments.add("test*.csv");
        arguments.add("[A:numeric,B:numeric,C:numeric]");
        arguments.add("-bin=\"" + fastBitBin + "\"");
        arguments.add("-delimiter=\",\"");
        arguments.add(opt);
//        arguments.add("-verbose");
        args = toArray(arguments);
        RDI.run(args);

//        access
        arguments = new ArrayList<>();
        arguments.add("FASTBIT:ACCESS");
        arguments.add("di_fb_idx-" + indexerName + "-" + idx);
        arguments.add(path);
        arguments.add("FastBit_"+indexFolderName);
        arguments.add("[ROWID:5,A,B,C]");
        arguments.add("-bin=\"" + fastBitBin + "\"");
        arguments.add("-delimiter=\",\"");
//        arguments.add("-verbose");
        args = toArray(arguments);
        RDI.run(args);
    }
    
    private static void optimizedFastBitIndexer(String[] args, String opt, int idx, String indexerName, String indexFolderName) {
        try {
            Utils.runCommand("rm -rf indexes*", path, false);
        } catch (IOException ex) {
            Logger.getLogger(UnitTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(UnitTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Utils.print(0, "----------------------------");
        Utils.print(0, "[Indexing] Optimized FastBit Cartridge (" + indexerName + ")");

//        extraction
        List<String> arguments = new ArrayList<>();
        arguments.add("OPTIMIZED_FASTBIT:INDEX");
        arguments.add("di_ofb_idx-" + indexerName + "-" + idx);
        arguments.add(path);
        arguments.add("test*.csv");
        arguments.add("[A:numeric,B:numeric]");
        arguments.add("-bin=\"" + fastBitBin + "\"");
        arguments.add("-delimiter=\",\"");
        arguments.add(opt);
//        arguments.add("-verbose");
        args = toArray(arguments);
        RDI.run(args);

//        access
        arguments = new ArrayList<>();
        arguments.add("OPTIMIZED_FASTBIT:ACCESS");
        arguments.add("di_ofb_idx-" + indexerName + "-" + idx);
        arguments.add(path);
        arguments.add("OptimizedFastBit_"+indexFolderName);
        arguments.add("[A,B]");
        arguments.add("-bin=\"" + fastBitBin + "\"");
        arguments.add("-delimiter=\",\"");
//        arguments.add("-verbose");
        args = toArray(arguments);
        RDI.run(args);
    }

    public static void postgresRAWIndexer() {
        try {
            //INDEX
            PostgresRAWIndexer postgres = new PostgresRAWIndexer("postgresraw", postgresRAWBin, postgresRAWpgDataDir);
            boolean isRunning = postgres.postgresRAWisRunning();
            if (isRunning) {
                postgres.stopPostgresRAW();
                Thread.sleep(3_000);
            }

            File pgDataDir = new File(postgresRAWpgDataDir);
            if (pgDataDir.exists()) {
                Utils.runCommand("rm -rf '" + postgresRAWpgDataDir + "'", path, false);
            }

            postgres.createPGDataDirIfNotExists();
            String[] argsIndex = {
                "POSTGRES_RAW:INDEX",
                "di_csv_idx_postgres_raw",
                path,
                "test1.csv",
                "[A:numeric,B:numeric:5,C:numeric:15]",
                "-delimiter=\",\"",
                "-bin=\"" + postgresRAWBin +"\"",
                "-pgData=\""+postgresRAWpgDataDir+"\"",
                "-verbose",
                "-option=[header:true]"
            };
            RDI.main(argsIndex);
            //ACCESS
            String[] argsAcess = {
                "POSTGRES_RAW:ACCESS",
                "di_csv_idx_postgres_raw",
                path,
                "test1.csv",
                "[A,B]",
                "-delimiter=\",\"",
                "-bin=\"" + postgresRAWBin + "\"",
                "-delimiter=\",\"",
                "-verbose"            
            };
            RDI.main(argsAcess);
            if (isRunning) {
                postgres.startPostgresRAW();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
