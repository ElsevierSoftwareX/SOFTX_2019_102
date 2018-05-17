package extractor;

import enumeration.extractor.AttributeType;
import enumeration.extractor.ExtensionType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
public class CSVExtractor extends Extractor {

    private BufferedWriter obw = null;
    private boolean firstTime = true;

    protected CSVExtractor(ExtensionType extension) {
        super(extension);
    }

    public CSVExtractor() {
        this(ExtensionType.CSV);
    }

    @Override
    public String run(String path, String fileName,
            HashMap<String, AttributeType> attributes, ArrayList<String> keys) {
        try {
            String[] strFiles = Utils.getFiles(path, fileName);

            File efile = new File(path + getExtractionFile());
            efile.createNewFile();
            this.obw = new BufferedWriter(new FileWriter(efile));

//            header
            boolean firstAtt = true;
            for (String att : attributes.keySet()) {
                if(firstAtt){
                    obw.write(att);
                    firstAtt = false;
                }else{
                    obw.write(Utils.ELEMENT_SEPARATOR + att);
                }
            }
            obw.write(Utils.NEW_LINE);
            obw.flush();

            for (String filename : strFiles) {
                extract(path, filename, attributes, keys);
            }
            obw.close();
            firstTime = true;

            return path + getExtractionFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return path + getExtractionFile();
    }

    @Override
    public void extract(String path, String fileName,
            HashMap<String, AttributeType> attributes, ArrayList<String> keys) {
        FileReader fr = null;
        try {
            File file = new File(path + fileName);
            fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            if (line != null) {
//                next lines
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(delimiter);

                    boolean firstValue = true;
                    for (int step=0; step<attributes.size(); step++) {
                        if (firstValue) {
                            firstValue = false;
                        } else {
                            obw.write(Utils.ELEMENT_SEPARATOR);
                        }
                        obw.write(values[step]);
                    }
                    obw.write(Utils.NEW_LINE);
                    obw.flush();
                }
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
}
