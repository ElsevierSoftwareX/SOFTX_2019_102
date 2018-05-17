package indexer;

import enumeration.indexer.AttributeType;
import enumeration.indexer.ExtensionType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import rdi.utils.Utils;

/**
 *
 * @author vitor
 */
public class CSVIndexer extends Indexer {

    private BufferedWriter obw = null;
    private boolean firstTime = true;

    protected CSVIndexer(ExtensionType extension, String indexerName) {
        super(extension, indexerName);
    }

    public CSVIndexer(String indexerName) {
        this(ExtensionType.CSV, indexerName);
    }

    @Override
    public String run(String path, String fileName,
            String[] attributeMappings, ArrayList<String> keys) {
        try {
            setAttributeMappings(attributeMappings);
            HashMap<String, AttributeType> attributes = getAttributes(attributeMappings);
            
            String[] strFiles = Utils.getFiles(path, fileName);

            File efile = new File(path + getIndexerFile());
            efile.createNewFile();
            obw = new BufferedWriter(new FileWriter(efile));

            firstTime = true;
            for (String filename : strFiles) {
                index(path, filename, attributes, keys);
                firstTime = false;
            }
            obw.close();
            firstTime = true;
            return path + getIndexerFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public void index(String path, String fileName, 
            HashMap<String, AttributeType> attributes, ArrayList<String> keys) {
        FileReader fr = null;
        try {
            File file = new File(path + fileName);
            fr = new FileReader(file);
            BufferedReader br = new BufferedReader(new FileReader(file));
            //            first line
            String line = br.readLine();
            int position = 1;
            if (line != null) {
                ArrayList<String> cols = getColumns(line);
                position += line.getBytes().length + 1;
                
                int rSize = attributes.size();
                if (rSize > 0 && cols.size() > 0) {
                    if (firstTime) {
                        obw.write(getFileNameAttribute());
                    }
                    
                    HashMap<Integer, Integer> attColPositions = new HashMap<>();
                    for (int i = 0; i < cols.size(); i++) {
                        String col = cols.get(i);
                        col = col.replaceAll("\"", "");
                        Map.Entry<String, AttributeType> currentAtt = Utils.containAttribute(attributes, col.toUpperCase());
                        if (currentAtt != null) {
                            if (firstTime) {
                                obw.write(";" + currentAtt.getKey());
                            }
                            if (keys.contains(col)) {
                                attColPositions.put(i, 1);
                            } else {
                                attColPositions.put(i, 0);
                            }
                        }
                    }
                    
                    while ((line = br.readLine()) != null) {
                        obw.write(Utils.NEW_LINE);
                        obw.write(Utils.getFileNameAttributeValue(path + fileName));
                        extractTuple(obw, attColPositions, line, position);
                        position += line.getBytes().length + 1;
                    }
                }
            }   br.close();
            fr.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSVIndexer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CSVIndexer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(CSVIndexer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void extractTuple(BufferedWriter obw, HashMap<Integer, Integer> attColPositions, String line, int initialPos) throws IOException {
        int currentColPosition = 0;
        String[] cStr = line.split(",");
        char[] cArray = line.toCharArray();
        int lastPos = initialPos;
        int currentPos = initialPos - 1;

        for (int c = 0; c < cArray.length; c++) {
            currentPos++;
            if (cArray[c] == delimiter.charAt(0)) {
                if (attColPositions.containsKey(currentColPosition)) {
                    if (attColPositions.get(currentColPosition) == 0) {
                        obw.write(";" + lastPos);
                    } else {
                        obw.write(";" + cStr[currentColPosition]);
                    }
                }
                lastPos = currentPos + 1;
                currentColPosition++;
            }
        }

        if (!line.isEmpty() && lastPos <= currentPos) {
            if (attColPositions.containsKey(currentColPosition)) {
                if (attColPositions.get(currentColPosition) == 0) {
                    obw.write(";" + lastPos);
                } else {
                    obw.write(";" + cStr[currentColPosition]);
                }
            }
        }
    }

    private ArrayList<String> getColumns(String line) {
        ArrayList<String> cols = new ArrayList<String>();

        char[] cArray = line.toCharArray();
        String temp = "";

        for (int c = 0; c < cArray.length; c++) {
            if (cArray[c] == delimiter.charAt(0)) {
                cols.add(temp);
                temp = "";
            } else {
                temp += cArray[c];
            }
        }

        if (!temp.trim().isEmpty()) {
            cols.add(temp);
        }

        return cols;
    }

    @Override
    public String access(String filePath, String fileName,
            HashMap<String, String> valueMappings, ArrayList<String> keys) {
        try {
            File aFile = new File(filePath + getAccessFile());
            aFile.createNewFile();
            BufferedWriter obuffer = new BufferedWriter(new FileWriter(aFile));

            boolean first = true;
            for (String key : valueMappings.keySet()) {
                if (first) {
                    first = false;
                } else {
                    obuffer.write(";");
                }
                obuffer.write(key);
            }
            obuffer.write(Utils.NEW_LINE);

            first = true;
            File efile = null;
            FileInputStream fis = null;

            for (Map.Entry<String, String> vm : valueMappings.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    obuffer.write(";");
                }

                if (keys.contains(vm.getKey())) {
                    obuffer.write(vm.getValue());
                } else {
                    efile = new File(filePath + fileName);
                    fis = new FileInputStream(efile);

                    int initialPos = Integer.parseInt(vm.getValue());
                    fis.skip(initialPos - 1);

                    byte[] slice = new byte[10];
                    fis.read(slice, 0, slice.length);
                    String sliceValue = new String(slice);

                    if(sliceValue.contains(delimiter) && sliceValue.contains(Utils.NEW_LINE)){
                        if(sliceValue.indexOf(delimiter) < sliceValue.indexOf(Utils.NEW_LINE)){
                            obuffer.write(sliceValue.split(delimiter)[0]);
                        }else if (sliceValue.indexOf(delimiter) > sliceValue.indexOf(Utils.NEW_LINE)){
                            obuffer.write(sliceValue.split(Utils.NEW_LINE)[0]);
                        }else{
                            obuffer.write(sliceValue);
                        }
                    }else {
                        obuffer.write(sliceValue);
                    }

                    fis.close();
                }
            }
            obuffer.write(Utils.NEW_LINE);
            obuffer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSVIndexer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CSVIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return filePath + getAccessFile();
    }
}
