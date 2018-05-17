package main;

import extractor.Extractor;
import enumeration.extractor.AttributeType;
import enumeration.extractor.ExtensionType;
import enumeration.extractor.OperationType;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author vitor
 */
public class RawDataExtractor {

    public static void main(String[] args) {
        run(args);
    }

    public static void run(String[] args) {
        if (args.length >= 4) {
            String[] extraction = args[0].split(":");
            ExtensionType extension = ExtensionType.valueOf(extraction[0].toUpperCase());
            OperationType operation = OperationType.valueOf(extraction[1].toUpperCase());

            String extractorName = args[1];
            String path = args[2];
            if (!path.endsWith(File.separator)) {
                path += File.separator;
            }
            String fileName = args[3];

            String delimiter = ";";
            String binDir = null;
            boolean verbose = false;
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
                    
                    if (args[i].startsWith("-bin")) {
                        binDir = args[i].replaceAll("-bin=", "");
                        if (binDir.startsWith("\"")) {
                            binDir = binDir.substring(1);
                        }
                        if (binDir.endsWith("\"")) {
                            binDir = binDir.substring(0, binDir.length() - 1);
                        }
                    }
                    
                    if (args[i].equals("-verbose")) {
                        verbose = true;
                    }
                }
            }

            Extractor rde = Extractor.newInstance(extension);
            if (rde == null) {
                System.out.println("The selected cartridge does not exist in Raw Data Extractor component!");
                System.exit(0);
            }

            rde.setName(extractorName);
            rde.setDelimiter(delimiter);
            rde.setVerbose(verbose);
            if (binDir != null) {
                rde.setBinaryDir(binDir);
            }

            if (operation == OperationType.EXTRACT) {
                HashMap<String, AttributeType> attributes = new HashMap<>();
                ArrayList<String> keys = new ArrayList<>();

                String[] attributeMappings = args[4].replaceAll("\\[", "").replaceAll("]", "").split(",");
                for (String map : attributeMappings) {
                    String[] slices = map.split(":");
                    attributes.put(slices[0].toUpperCase(), AttributeType.valueOf(slices[1].toUpperCase()));
                    if (slices.length > 2 && slices[2].toLowerCase().equals("key")) {
                        keys.add(slices[0].toUpperCase());
                    }
                }

                long tStart = System.currentTimeMillis();
                String result = rde.run(path, fileName, attributes, keys);
                long tEnd = System.currentTimeMillis();
                double elapsedSeconds = (tEnd - tStart) / 1000.0;
                System.out.println(String.format("%s:EXTRACT --> %.2f seconds", rde.getExtensionToString(), elapsedSeconds));

                if (rde.isVerbose()) {
                    System.out.println(result);
                }
            } else if (operation == OperationType.ACCESS) {
                long tStart = System.currentTimeMillis();
                String result = rde.access(path, fileName);
                long tEnd = System.currentTimeMillis();
                double elapsedSeconds = (tEnd - tStart) / 1000.0;
                System.out.println(String.format("%s:ACCESS --> %.2f seconds", rde.getExtensionToString(), elapsedSeconds));
                if (result != null) {
                    System.out.println(result);
                } else {
                    System.out.println("[Operation Result] It was executed unsuccessfully!");
                }
            }
        }
    }

}
