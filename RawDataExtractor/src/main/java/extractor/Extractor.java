package extractor;

import enumeration.extractor.AttributeType;
import enumeration.extractor.ExtensionType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vitor
 */
public abstract class Extractor {

    private final ExtensionType extension;
    private String name;
    protected String delimiter;
    protected String binaryDir;
    protected boolean verbose = false;

    public static Extractor newInstance(ExtensionType extension) {
        switch (extension) {
            case PROGRAM:
                return new ExternalProgram();
            case CSV:
                return new CSVExtractor();
        }

        return null;
    }

    public Extractor(ExtensionType extension) {
        this.extension = extension;
    }

    public String getExtractionFile() {
        return getName() + ".data";
    }

    public String getExtensionToString() {
        return extension.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileNameAttribute() {
        return ("FILENAME").toUpperCase();
    }

    public String getAccessFile() {
        return getName() + ".access";
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getPathNameAttribute() {
        return ("FILEPATH").toUpperCase();
    }

    public String getRowIDAttribute() {
        return ("ROWID").toUpperCase();
    }

    public void setBinaryDir(String binDir) {
        this.binaryDir = binDir;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    public boolean isVerbose(){
        return this.verbose;
    }

    public abstract String run(String path, String fileName, 
            HashMap<String, AttributeType> attributes, ArrayList<String> keys);
    
    public abstract void extract(String path, String fileName, 
            HashMap<String, AttributeType> attributes, ArrayList<String> keys);
    
    public String access(String filePath, String fileName) {
        if(verbose){
            FileReader fr = null;
            try {
                File file = new File(filePath + fileName);
                fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String line = br.readLine();
                while(line != null){
                    System.out.println(line);
                    line = br.readLine();
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CSVExtractor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CSVExtractor.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fr.close();
                } catch (IOException ex) {
                    Logger.getLogger(CSVExtractor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return filePath + fileName;
    }

}
