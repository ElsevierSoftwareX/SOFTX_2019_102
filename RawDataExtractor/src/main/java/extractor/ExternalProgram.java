package extractor;

import enumeration.extractor.AttributeType;
import enumeration.extractor.ExtensionType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import rde.utils.Utils;

/**
 *
 * @author vitor
 */
public class ExternalProgram extends Extractor {

    protected ExternalProgram(ExtensionType extension) {
        super(extension);
    }

    public ExternalProgram() {
        this(ExtensionType.PROGRAM);
    }
    
    @Override
    public String run(String path, String fileName, 
            HashMap<String, AttributeType> attributes, ArrayList<String> keys){
        extract(path, fileName, attributes, keys);
        return path + getExtractionFile();
    }
    
    @Override
    public void extract(String path, String fileName, 
            HashMap<String, AttributeType> attributes, ArrayList<String> keys){
        try {
            Utils.runCommand(fileName, path, verbose);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ExternalProgram.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String run(String path, String fileName){
        extract(path, fileName);
        return path + getExtractionFile();
    }
    
    public void extract(String path, String fileName){
        try {
            Utils.runCommand(fileName, path, verbose);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ExternalProgram.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
