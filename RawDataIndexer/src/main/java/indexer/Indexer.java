package indexer;

import enumeration.indexer.AttributeType;
import enumeration.indexer.ExtensionType;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author vitor
 */
public abstract class Indexer {

    private final ExtensionType extension;
    private String name;
    protected String delimiter = ";";
    protected String binaryDir;
    protected HashMap<String, String> idxOptions;
    protected boolean verbose = false;
    protected String performanceFilePath = null;
    private String[] attributeMappings;
    private String pgData;

    public static Indexer newInstance(ExtensionType extension, String indexerName) {
        switch (extension) {
            case CSV:
                return new CSVIndexer(indexerName);
            case FASTBIT:
                return new FastBitIndexer(indexerName);
            case OPTIMIZED_FASTBIT:
                return new OptimizedFastBitIndexer(indexerName);
            case FITS:
                return new FITSIndexer(indexerName);
            case POSTGRES_RAW:
                return new PostgresRAWIndexer(indexerName);
        }

        return null;
    }

    public Indexer(ExtensionType extension, String indexerName) {
        this.extension = extension;
        this.name = indexerName;
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
        return ("FILENAME").toUpperCase();
    }

    public String getRowIDAttribute() {
        return ("ROWID").toUpperCase();
    }

    public void setBinaryDir(String binDir) {
        this.binaryDir = binDir;
    }

    public void setIdxOptions(HashMap<String, String> idxOptions) {
        this.idxOptions = idxOptions;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public String getIndexerFile() {
        return getName() + ".index";
    }

    public boolean isVerbose() {
        return verbose;
    }

    public String[] getAttributeMappings() {
        return attributeMappings;
    }

    public void setAttributeMappings(String[] attributeMappings) {
        this.attributeMappings = attributeMappings;
    }
    
    public abstract String run(String path, String fileName,
            String[] attributes, ArrayList<String> keys);

    public abstract void index(String path, String fileName,
            HashMap<String, AttributeType> attributes, ArrayList<String> keys);

    public abstract String access(String filePath, String fileName,
            HashMap<String, String> valueMappings, ArrayList<String> keys);

    public void setPerformanceFilePath(String performanceFilePath) {
        this.performanceFilePath = performanceFilePath;
    }

    public boolean hasPerformanceFilePath() {
        return (performanceFilePath != null);
    }

    public String getPgData() {
        return pgData;
    }

    public void setPgData(String pgData) {
        this.pgData = pgData;
    }
    
    public void writePerformanceData(double elapsedSeconds) {
        try {
            FileWriter fw = new FileWriter(performanceFilePath,true); //the true will append the new data
            fw.write("Indexing:RDI:Index\n      elapsed-time: " + elapsedSeconds + " seconds.\n");//appends the string to the file
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected HashMap<String, AttributeType> getAttributes(String[] attributeMappings) {
        HashMap<String, AttributeType> attributes = new HashMap<>();
        for(String att : attributeMappings){
            String[] split = att.split(":");
            attributes.put(split[0], AttributeType.valueOf(split[1].toUpperCase()));
        }
        return attributes;
    }

}
