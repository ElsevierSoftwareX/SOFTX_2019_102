package main;

import indexer.Indexer;
import enumeration.indexer.AttributeType;
import enumeration.indexer.ExtensionType;
import enumeration.indexer.OperationType;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author vitor
 */
public class RDI {

    public static void main(String[] args) {
        run(args);
    }

    public static void run(String[] args) {
        if (args.length > 4) {
            String[] indexing = args[0].split(":");
            ExtensionType extension = ExtensionType.valueOf(indexing[0].toUpperCase());
            OperationType operation = OperationType.valueOf(indexing[1].toUpperCase());            

            String indexerName = args[1];
            String path = args[2];
            if (!path.endsWith(File.separator)) {
                path += File.separator;
            }

            /*
            *To suport "*" coming from command line, because for accept this kind of
            *file name (*.csv) it's need put the regex pattern inside of double quotes  
            */ 
            String fileName = args[3].replaceAll("\"", "");

            String delimiter = ";";
            String binDir = null;
            String pgData = null;
            HashMap<String, String> idxOptions = new HashMap<>();
            boolean verbose = false;
            String performanceFilePath = null;
            if (args.length > 5) {
                for (int i = 5; i < args.length; i++) {
                    if (args[i].startsWith("-delimiter=")) {
                        delimiter = args[i].replaceAll("-delimiter=", "");
                        if (delimiter.startsWith("\"")) {
                            delimiter = delimiter.substring(1);
                        }
                        if (delimiter.endsWith("\"")) {
                            delimiter = delimiter.substring(0, delimiter.length() - 1);
                        }
                    }

                    if (args[i].startsWith("-bin")) {
                        binDir = args[i].replaceAll("-bin=", "");
                        if (binDir.startsWith("\"")) {
                            binDir = binDir.substring(1);
                        }
                        if (binDir.endsWith("\"")) {
                            binDir = binDir.substring(0, binDir.length() - 1);
                        }
                    }

                    if (args[i].startsWith("-pgData")) {
                        pgData = args[i].replaceAll("-pgData=", "");
                        if (pgData.startsWith("\"")) {
                            pgData = pgData.substring(1);
                        }
                        if (pgData.endsWith("\"")) {
                            pgData = pgData.substring(0, pgData.length() - 1);
                        }
                    }
                    
                    if (args[i].startsWith("-option")) {
                        String[] options = args[i].replaceAll("-option=", "").replaceAll("\\[", "").replaceAll("]", "").split(",");     
                        for (String opt: options) {
                            String [] slices = opt.split(":");
                            for (int j = 0; j < slices.length; j++) {                                
                                idxOptions.put(slices[0], slices[1]);
                            }                    
                        }
                    }
                    
                    if (args[i].startsWith("-performance")) {
                        performanceFilePath = args[i].replaceAll("-performance=", "");     
                    }

                    if (args[i].equals("-verbose")) {
                        verbose = true;
                    }
                }
            }

            Indexer rdi = Indexer.newInstance(extension, indexerName);
            if (rdi == null) {
                System.out.println("The selected cartridge does not exist in Raw Data Extractor component!");
                System.exit(0);
            }
            rdi.setDelimiter(delimiter);
            rdi.setVerbose(verbose);
            if (binDir != null) {
                rdi.setBinaryDir(binDir);
            }
            if (extension == ExtensionType.POSTGRES_RAW) {
                if (operation == OperationType.INDEX && pgData == null) {
                    System.err.println("Enter the pgData directory");
                    return;
                } else {
                    rdi.setPgData(pgData);
                }
            }
            if (!idxOptions.isEmpty()) {
                rdi.setIdxOptions(idxOptions);
            }
            if (performanceFilePath!=null){
                rdi.setPerformanceFilePath(performanceFilePath);
            }

            if (operation == OperationType.INDEX) {
                HashMap<String, AttributeType> attributes = new HashMap<>();
                ArrayList<String> keys = new ArrayList<>();

                String[] attributeMappings = args[4].replaceAll("\\[", "").replaceAll("]", "").split(",");
                rdi.setAttributeMappings(attributeMappings);
                for (String map : attributeMappings) {
                    String[] slices = map.split(":");
                    attributes.put(slices[0].toUpperCase(), AttributeType.valueOf(slices[1].toUpperCase()));
                    if (slices.length > 2 && slices[2].toLowerCase().equals("key")) {
                        keys.add(slices[0].toUpperCase());
                    }
                }

                long tStart = System.currentTimeMillis();
                String result = rdi.run(path, fileName, attributeMappings, keys);
                long tEnd = System.currentTimeMillis();
                double elapsedSeconds = (tEnd - tStart) / 1000.0;
                System.out.println(String.format("%s:INDEX --> %.2f seconds", rdi.getExtensionToString(), elapsedSeconds));
                if(rdi.hasPerformanceFilePath()){
                    rdi.writePerformanceData(elapsedSeconds);
                }

                if (rdi.isVerbose()) {
                    System.out.println(result);
                }
            } else if (operation == OperationType.ACCESS) {
                HashMap<String, String> values = new HashMap<>();
                ArrayList<String> keys = new ArrayList<>();

                String[] valueMappings = args[4].replaceAll("\\[", "").replaceAll("]", "").split(",");
                for (String map : valueMappings) {
                    String[] slices = map.split(":");
                    if (slices.length < 2) {
                        values.put(slices[0], null);
                    } else {
                        values.put(slices[0], slices[1]);
                    }
                    if (slices.length > 2 && slices[2].toLowerCase().equals("key")) {
                        keys.add(slices[0].toUpperCase());
                    }
                }

                long tStart = System.currentTimeMillis();
                String result = rdi.access(path, fileName, values, keys);
                long tEnd = System.currentTimeMillis();
                double elapsedSeconds = (tEnd - tStart) / 1000.0;
                System.out.println(String.format("%s:ACCESS --> %.2f seconds", rdi.getExtensionToString(), elapsedSeconds));
                if (result != null) {
                    System.out.println(result);
                } else {
                    System.out.println("[Operation Result] It was executed unsuccessfully!");
                }
            }
        }
    }

}
