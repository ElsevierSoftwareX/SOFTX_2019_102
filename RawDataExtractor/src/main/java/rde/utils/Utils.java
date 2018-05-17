package rde.utils;

import enumeration.extractor.AttributeType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tools.ant.DirectoryScanner;

/**
 *
 * @author vitor
 */
public class Utils {

    public static String indentationContent = "  ";
    public final static String DIR_SEPARATOR = File.separator;
    public final static String SEPARATOR = "-";
    public final static String ELEMENT_SEPARATOR = ";";
    public final static String NEW_LINE = "\n";

    public static void print(int indentationLevel, String content) {
        for (int i = 0; i < indentationLevel; i++) {
            System.out.print(indentationContent);
        }
        System.out.println(content);
    }

    public static String getPathFromFile(File f) {
        return f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(File.separator) + 1);
    }

    public static void deleteDirectory(File dfDir) {
        if (dfDir.isDirectory()) {
            try {
                Utils.runCommand("rm -rf " + dfDir.getAbsolutePath(), ".", false);
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static int runCommand(String cmd, String dir, boolean verbose) throws IOException, InterruptedException {
        Runtime run = Runtime.getRuntime();
        int result;
        String command[];
        if (Utils.isWindows()) {
            String cmdWin[] = {"cmd.exe", "/c", cmd};
            command = cmdWin;
        } else {
            String cmdLinux = cmd;
            if (cmd.contains(">")) {
                cmdLinux = cmd.replace(">", ">>");
            }
            String cmdLin[] = {"/bin/bash", "-c", cmdLinux};
            command = cmdLin;
        }
        if (verbose) {
            System.out.println(command[command.length - 1]);
        }
        Process pr;
        if (dir == null) {
            pr = run.exec(command);
        } else {
            pr = run.exec(command, null, new File(dir));
        }

        pr.waitFor();
        result = pr.exitValue();
        pr.destroy();

        return result;
    }

    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.contains("win"));
    }
    
    public static String readConfigurationFile() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("DfA.properties"));
            String line;
            while (br.ready()) {
                line = br.readLine();
                String[] slices = line.split("=");
                
                if (slices[0].equals("di_dir")) {
                    return (slices[1] + Utils.DIR_SEPARATOR);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static String[] getFiles(String workspace, String search) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[]{search});
        scanner.setBasedir(workspace);
        scanner.setCaseSensitive(true);
        scanner.scan();
        return scanner.getIncludedFiles();
    }

    public static Map.Entry<String, AttributeType> containAttribute(HashMap<String, AttributeType> attributes, String attName) {
        for(Map.Entry<String, AttributeType> att : attributes.entrySet()){
            if(att.getKey().equals(attName.toUpperCase())){
                return att;
            }
        }
        
        return null;
    }

    public static void printIllegalOperation(String message) {
        print(0, "[Illegal Operation] " + message);
    }

    public static String getFileNameAttributeValue(String filePath) {
        return ("'" + filePath + "'");
    }
}
