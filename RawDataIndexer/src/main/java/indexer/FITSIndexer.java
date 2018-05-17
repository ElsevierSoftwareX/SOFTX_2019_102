package indexer;

import enumeration.indexer.AttributeType;
import enumeration.indexer.ExtensionType;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author vitor
 */
public class FITSIndexer extends Indexer {

    protected FITSIndexer(ExtensionType extension, String indexerName) {
        super(extension, indexerName);
    }

    public FITSIndexer(String indexerName) {
        this(ExtensionType.FITS, indexerName);
    }

    @Override
    public String run(String path, String fileName, String[] attributeMappings, ArrayList<String> keys) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void index(String path, String fileName, HashMap<String, AttributeType> attributes, ArrayList<String> keys) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String access(String filePath, String fileName, HashMap<String, String> valueMappings, ArrayList<String> keys) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
