package indexer;

import enumeration.indexer.AttributeType;
import enumeration.indexer.ExtensionType;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import rdi.utils.Utils;

/**
 *
 * @author vitor, zevitor
 */
public class FastBitIndexer extends Indexer {

    private String strIdxOpt;
    private String indexFolderName;
    private String baseDirectory;
    private BufferedWriter obw = null;
    private boolean firstTime = true;

    protected FastBitIndexer(ExtensionType extension, String indexerName) {
        super(extension, indexerName);
    }

    public FastBitIndexer(String indexerName) {
        this(ExtensionType.FASTBIT, indexerName);
    }

    @Override
    public String run(String path, String fileName,
            String[] attributeMappings, ArrayList<String> keys) {        
        try {
            setAttributeMappings(attributeMappings);
            HashMap<String, AttributeType> attributes = getAttributes(attributeMappings);
            
            loadIndexOptionsAndIndexFolderName();
            baseDirectory = new File(new File(path).getAbsoluteFile(),"FastBit_"+indexFolderName).getAbsolutePath();
            String[] strFiles = Utils.getFiles(path, fileName);

            File efile = new File(path + getIndexerFile());
            efile.createNewFile();
            obw = new BufferedWriter(new FileWriter(efile));

            firstTime = true;

            for (String filename : strFiles) {
                index(path, filename, attributes, keys);
                firstTime = false;
            }
            obw.flush();
            obw.close();
            firstTime = true;

            return path + getIndexerFile();
        } catch (IOException ex) {
            Logger.getLogger(FastBitIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void index(String path, String fileName,
            HashMap<String, AttributeType> attributes, ArrayList<String> keys) {
        try {
            String indexFile =  new File(new File(baseDirectory),Utils.getFilenameWithoutExtension(fileName)).getAbsolutePath();
            List<Object> returnList = new ArrayList<>();
            HashMap<String, String> columns = new HashMap<>();

            returnList = handleCSVHeader(path + fileName, '#');

            char firstChar = (char) returnList.get(1);
            String firstTuple = (String) returnList.get(2);
            columns = getColumns(attributes, (ArrayList<String>) returnList.get(0), firstTuple);

            String nrows = "0";
            if (firstTuple != null) {
                callArdea(indexFile, columns, path + fileName);

                nrows = readNumberRows(indexFile);
                writeRowIDFile(indexFile, nrows);

                callIbis(indexFile, strIdxOpt);

                if (firstChar != '#') {
                    handleCSVHeader(path + fileName, firstChar);
                }

                writeOutputFile(attributes, columns, keys, path, indexFile, nrows);
            }

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FastBitIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Método para criar arquivo bruto com a coluna RowID
    private void writeRowIDFile(String directory, String nrows) throws IOException {

        File nFile = new File(directory + "/RowID");
        String refFile = "/FastBit/rid";

        try (FileOutputStream out = new FileOutputStream(nFile);
                BufferedOutputStream bfout = new BufferedOutputStream(out)) {
            byte[] bytes = getDefaultBytes(getClass().getResourceAsStream(refFile));
            int len = bytes.length;
            int byte1 = 0;
            int byte2 = 0;
            int byte3 = 0;
            int byte4 = 0;

            for (int i = 1; i <= Integer.parseInt(nrows); i++) {
                if (i < len) {
                    bfout.write(bytes, i, 1);
                    bfout.write(bytes, 0, 1);
                    bfout.write(bytes, 0, 1);
                    bfout.write(bytes, 0, 1);
                } else if (i >= len && i < (len * len)) {
                    if (i % len == 0) {
                        byte1 = 0;
                        byte2++;
                    }
                    bfout.write(bytes, (byte1), 1);
                    bfout.write(bytes, (byte2), 1);
                    bfout.write(bytes, 0, 1);
                    bfout.write(bytes, 0, 1);
                    byte1++;
                } else if (i >= (len * len) && i < (len * len * len)) {
                    if (i % len == 0) {
                        byte1 = 0;
                        byte2++;
                    }
                    if (i % (len * len) == 0) {
                        byte2 = 0;
                        byte3++;
                    }
                    bfout.write(bytes, (byte1), 1);
                    bfout.write(bytes, (byte2), 1);
                    bfout.write(bytes, (byte3), 1);
                    bfout.write(bytes, 0, 1);
                    byte1++;
                } else {
                    if (i % len == 0) {
                        byte1 = 0;
                        byte2++;
                    }
                    if (i % (len * len) == 0) {
                        byte2 = 0;
                        byte3++;
                    }
                    if (i % (len * len * len) == 0) {
                        byte3 = 0;
                        byte4++;
                    }
                    bfout.write(bytes, (byte1), 1);
                    bfout.write(bytes, (byte2), 1);
                    bfout.write(bytes, (byte3), 1);
                    bfout.write(bytes, (byte4), 1);
                    byte1++;
                }
            }

            bfout.flush();
            out.close();
        }

        modifyMetaFile(directory);
    }

    @Override
    public String access(String path, String indexDir,
            HashMap<String, String> values, ArrayList<String> keys) {
        try {
            List<String> atts = new ArrayList<String>();
            for (Map.Entry<String, String> vm : values.entrySet()) {
                if (!keys.contains(vm.getKey().toUpperCase()) && !vm.getKey().toUpperCase().equals("ROWID")) {
                    atts.add(vm.getKey().toUpperCase());
                }
            }

            String rid = values.get("ROWID");
            getFieldValueIbis(path, indexDir, atts, rid);

            return path + getAccessFile();
        } catch (IOException ex) {
            Logger.getLogger(FastBitIndexer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(FastBitIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return path + getAccessFile();
    }

    private String extractTuple(HashMap<String, AttributeType> attributes, HashMap<String, String> columns, String directory, int row) {
        String tuple =  "'" + directory + "';" + String.valueOf(row + 1);

        for (String field : columns.keySet()) {
            if (attributes.containsKey(field)) {
                tuple = tuple + ";'" + field + ".idx'";;
            }
        }

        return tuple;
    }

    private String extractTuple(HashMap<String, AttributeType> attributes, HashMap<String, String> columns, ArrayList<String> keys, String directory, String keyValues, int row) {
        String tuple =  "'" + directory + "';" + String.valueOf(row + 1);

        if (!keyValues.isEmpty()) {
            String[] values = keyValues.split(", ");
            for (String field : columns.keySet()) {
                if (attributes.containsKey(field)) {
                    if (keys.contains(field)) {
                        tuple = tuple + ";" + values[keys.indexOf(field)];
                    } else {
                        tuple = tuple + ";'" + field + ".idx'";;
                    }
                }
            }
        }
        return tuple;
    }

    // Método para obter as colunas a serem indexadas e seus tipos
    private HashMap<String, String> getColumns(HashMap<String, AttributeType> attributes, ArrayList<String> colsNames, String firstTuple) {
        HashMap<String, String> columns = new LinkedHashMap<>();
        Map order = new LinkedHashMap();

        if (attributes.size() > 0 && colsNames.size() > 0) {
            // Ordenar as colunas
            for (String field : attributes.keySet()) {
                for (int c = 0; c < colsNames.size(); c++) {
                    String col = colsNames.get(c);
                    col = col.replaceAll("\"", "");
                    col = col.replaceAll("#", "");
                    col = col.toUpperCase();//When the column in the file is in lowercase, it's necessary to uppercase it for compare with field
                    if (col.equals(field)) {
                        order.put(c, field);
                    }
                }
            }
            ArrayList forGetIndexOfCols =  new ArrayList(attributes.keySet());
            for (int c = 0; c < colsNames.size(); c++) {
                // Adicionar campos não indexados
                if (!order.containsKey(c)) {
                    String fieldType = getFieldType(firstTuple, c);
                    columns.put(colsNames.get(c).toUpperCase(), fieldType);
                } else {
                    String field = order.get(c).toString();
                    String fieldType = null;
                    switch (attributes.get(field)) {
                        case TEXT:
                        case FILE:
                            fieldType = "text";
                            break;
                        case NUMERIC:
                            String[] columnData = getAttributeMappings()[forGetIndexOfCols.indexOf(field)].split(":");
                            if (AttributeType.getPrecisionOfNumber(columnData) > 0) {
                                fieldType = "float";
                            } else {
                                fieldType = "int";
                            }
                            break;
                    }                    
                    if (checkTypes(attributes.get(field), fieldType)) {
                        columns.put(field, fieldType);
                    } else {
                        columns.put(field, String.valueOf(attributes.get(field)));
                    }
                }
            }
        }

        return columns;
    }

    // Método para obter os nomes das colunas do arquivo CSV
    private ArrayList<String> getColumnsNames(String line) {
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

    // Método para executar a função ARDEA do FastBit
    private int callArdea(String directory, HashMap<String, String> columns, String csv) throws IOException, InterruptedException {
        String colString = "\"";
        for (String column : columns.keySet()) {
            colString = colString + column.replaceAll("\"", "") + ":" + columns.get(column) + ", ";
        }
        colString = colString.substring(0, colString.lastIndexOf(", ")) + "\"";
        String program = binaryDir + "/ardea -d " + directory
                + " -m " + colString + " -t " + csv + " -b \"" + delimiter + "\"";
        String[] cmd = {"/bin/bash", "-c", program};

        Process pr = Runtime.getRuntime().exec(cmd);
        BufferedReader brInput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        BufferedReader brError = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
        if (verbose) {
            System.out.println(program);
        }
        String line;
        while ((line = brInput.readLine()) != null) {
            if (verbose) {
                System.out.println(line);
            }
        }

        while ((line = brError.readLine()) != null) {
            if (verbose) {
                System.out.println(line);
            }
        }
        brInput.close();
        brError.close();

        pr.waitFor();
        int result = pr.exitValue();
        pr.destroy();

        return result;
    }

    // Método para executar a função IBIS do FastBit apenas para construção dos índices 
    private int callIbis(String directory, String strIdxOpt) throws IOException, InterruptedException {
        String program = binaryDir + "/ibis -v -d " + directory + " -b " + strIdxOpt;
        String[] cmd = {"/bin/bash", "-c", program};

        String line;
        Process pr = Runtime.getRuntime().exec(cmd);
        BufferedReader brInput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        BufferedReader brError = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
        while ((line = brInput.readLine()) != null) {
            if (verbose) {
                System.out.println(line);
            }
        }
        brInput.close();

        while ((line = brError.readLine()) != null) {
            if (verbose) {
                System.out.println(line);
            }
        }
        brError.close();

        pr.waitFor();
        int result = pr.exitValue();
        pr.destroy();

        return result;
    }

    // Método para montar o arquivo de saída do extrator
    private void writeOutputFile(HashMap<String, AttributeType> attributes, HashMap<String, String> columns,
            ArrayList<String> keys, String path, String directory, String nrows) throws IOException, InterruptedException {//aqui
        if (attributes.size() > 0 && columns.size() > 0) {
            if (firstTime) {
                String prefix = getPathNameAttribute() + ";" + getRowIDAttribute();
                obw.write(prefix);

                String separator = ";";
                for (String field : columns.keySet()) {
                    if (attributes.containsKey(field)) {
                        obw.write(separator + field.toUpperCase());
                    }
                }
                obw.write(Utils.NEW_LINE);
            }
            obw.flush();

            String tuple = "";
            if (!keys.isEmpty()) {
                String keysFile = getKeyFieldsIbis(path, keys);
                File kfile = new File(keysFile);
                BufferedReader br = new BufferedReader(new FileReader(kfile));

                for (int i = 0; i < Integer.parseInt(nrows); i++) {
                    String line = br.readLine();
                    tuple = extractTuple(attributes, columns, keys, directory, line, i);
                    obw.write(tuple);
                    obw.write(Utils.NEW_LINE);
                    obw.flush();
                }

                br.close();
                kfile.delete();
            } else {
                for (int i = 0; i < Integer.parseInt(nrows); i++) {
                    tuple = extractTuple(attributes, columns, directory, i);
                    obw.write(tuple);
                    obw.write(Utils.NEW_LINE);
                    obw.flush();
                }
            }
        }
    }

    // Método para executar a função IBIS do FastBit afim de obter os valores 
    // dos campos de Join. Ela gera um arquivo de saída (joinFields) com todos os valores.
    public String getKeyFieldsIbis(String directory, ArrayList<String> fields) throws IOException, InterruptedException {
        String query = "\"select ";
        for (String field : fields) {
            query = query + field.toUpperCase() + ",";
        }
        query = query.substring(0, query.lastIndexOf(",")) + "\"";

        String tempFile = directory + "/joinFields";
        String program = binaryDir + "/ibis -v -d "
                + directory + " -q " + query + " -o " + tempFile;
        String[] cmd = {"/bin/bash", "-c", program};

        String line;
        Process pr = Runtime.getRuntime().exec(cmd);
        BufferedReader brInput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        BufferedReader brError = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
        while ((line = brInput.readLine()) != null) {
            if (verbose) {
                System.out.println(line);
            }
        }
        brInput.close();

        while ((line = brError.readLine()) != null) {
            if (verbose) {
                System.out.println(line);
            }
        }
        brError.close();
        pr.waitFor();
        pr.exitValue();
        pr.destroy();

        return tempFile;
    }

    // Método para manipular o cabeçalho do arquivo CSV:
    // Recuperar os nomes das colunas e adicionar (ou retirar) o char '#' do cabeçalho,
    // necessário para execução correta da função ARDEA.
    private List<Object> handleCSVHeader(String path, char firstChar) throws FileNotFoundException, IOException {
        File file = new File(path);
        Reader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        List<Object> returnList = new ArrayList<>();

        if (firstChar == '#') {
            String line = br.readLine();
            ArrayList<String> columns = getColumnsNames(line);
            returnList.add(0, columns);

            if (line.charAt(0) != '#') {
                firstChar = line.charAt(0);
                returnList.add(1, firstChar);
                try (RandomAccessFile fw = new RandomAccessFile(file, "rw")) {
                    fw.seek(0); // to the beginning
                    fw.write("#".getBytes());
                }

                String firstTuple = br.readLine();
                returnList.add(2, firstTuple);

            } else {
                String firstTuple = br.readLine();
                returnList.add(1, firstChar);
                returnList.add(2, firstTuple);
            }

            br.close();
            return returnList;
        } else {
            String line = br.readLine();
            if (line.charAt(0) == '#') {
                try (RandomAccessFile fw = new RandomAccessFile(file, "rw")) {
                    fw.seek(0); // to the beginning
                    fw.write(String.valueOf(firstChar).getBytes());
                }
            }

            br.close();
            return returnList;
        }
    }

    // Método que executa a função IBIS do FastBit de modo a realizar uma consulta simples,
    // na qual um único campo é selecionado de acordo com a sua linha.
    // O valor é obtido ainda no System.out, sem arquivo de saída.
    private String getFieldValueIbis(String directory, String indexDir, List<String> fields, String row) throws IOException, InterruptedException {
        String query = "\"select ";
        boolean first = true;
        for (String f : fields) {
            if (first) {
                query += f;
                first = false;
            } else {
                query += "," + f;
            }
        }
        query += " WHERE ROWID = " + row + "\"";
        String program = binaryDir + Utils.DIR_SEPARATOR + "ibis -d \"" + directory
                + indexDir + "\" -q " + query + " -o";
        String[] cmd = {"/bin/bash", "-c", program};

        String result = "";
        String line;
        Process pr = Runtime.getRuntime().exec(cmd);
        BufferedReader brInput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        BufferedReader brError = new BufferedReader(new InputStreamReader(pr.getErrorStream()));

        File file = new File(directory + getAccessFile());
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        writeHeader(bw, fields);

        while ((line = brInput.readLine()) != null) {
//            System.out.println(line);
        }
        brInput.close();

        int counterLine = 0;
        while ((line = brError.readLine()) != null) {
            if (counterLine >= 3) {
                result = line;
                line = line.replaceAll(",", ";").replaceAll(" ", "");
//                System.out.println(line);
                bw.write(line + Utils.NEW_LINE);
                bw.flush();
            }
            counterLine++;
        }
        brError.close();

        bw.close();
        fw.close();

        pr.waitFor();
        pr.exitValue();
        pr.destroy();

        return result;
    }

    // Método para recuperar o número total de linhas do dado a partir do arquivo -part.txt
    private static String readNumberRows(String directory) throws FileNotFoundException, IOException {
        File file = new File(directory + "/-part.txt");
        Reader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String nrows = null;

        while (br.ready()) {
            String line = br.readLine();
            if (line.contains("Number_of_rows =")) {
                nrows = line.replace("Number_of_rows = ", "");
                return nrows;
            }
        }

        return nrows;
    }

    // Método para obter os bytes do raw file de referência
    private static byte[] getDefaultBytes(InputStream in) throws FileNotFoundException, IOException {
        byte[] buffer = new byte[1024];
        in.read(buffer);

        byte[] bytes = new byte[256];
        int iBytes = 0;

        for (int i = 0; i < buffer.length; i++) {
            if (i == 0 || i % 4 == 0) {
                bytes[iBytes] = buffer[i];
                iBytes++;
            }
        }
        return bytes;
    }

    // Método para incluir a coluna RowID no arquivo -part.txt
    private static void modifyMetaFile(String directory) throws FileNotFoundException, IOException {
        File file = new File(directory + "/-part.txt");
        File tempFile = new File(directory + "/tmp");
        tempFile.createNewFile();
        Reader fr;
        BufferedReader br;

        try (FileWriter fw = new FileWriter(tempFile)) {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String line = "";
            String ncolumns = "";
            while (br.ready()) {
                line = br.readLine();
                if (line.contains("Number_of_columns =")) {
                    ncolumns = line.replace("Number_of_columns = ", "");
                    line = line.replace(ncolumns, String.valueOf(Integer.parseInt(ncolumns) + 1));
                }

                fw.write(line + "\n");
            }

            fw.write("\nBegin Column\nname = RowID\ndata_type = INT\nEnd Column\n");
        }

        br.close();
        fr.close();

        file.delete();
        tempFile.renameTo(file);
    }

    // Método para verificar o tipo de um campo não especificado na indexação,
    // mas presente no arquivo CSV.
    private String getFieldType(String firstTuple, int order) {
        String fieldType = "text";
        if (firstTuple != null) {
            String[] tuple = firstTuple.split(delimiter);
            String value = tuple[order];

            if (isNumeric(value) && (value.length() < 9)) {
                if (value.contains(".") || value.contains(",")) {
                    fieldType = "float";
                } else {
                    fieldType = "int";
                }
            }
        }

        return fieldType;
    }

    // Método para correlacionar os tipos do FB com os AttributeType
    private boolean checkTypes(AttributeType att, String colType) {
        boolean check = false;
        String attType = String.valueOf(att);

        if (attType.equals(colType.toUpperCase())) {
            check = true;
        } else {
            if (attType.toUpperCase().equals("NUMERIC")
                    && (colType.equals("float") || colType.equals("int"))) {
                check = true;
            }
        }

        return check;
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?(\\,\\d+)?");
    }

    private void writeHeader(BufferedWriter bw, List<String> fields) throws IOException {
        boolean first = true;
        for (String f : fields) {
            if (first) {
                first = false;
                bw.write(f);
            } else {
                bw.write(Utils.ELEMENT_SEPARATOR + f);
            }
        }
        bw.write(Utils.NEW_LINE);
        bw.flush();
    }

    private void loadIndexOptionsAndIndexFolderName() {
        String binning = "precision=2";
        String encoding = "equality";
        String compressing = null;
        if ((idxOptions != null) && (!idxOptions.isEmpty())) {
            if (idxOptions.containsKey("b"))
                binning = idxOptions.get("b").trim();
            if (idxOptions.containsKey("e"))
                encoding = idxOptions.get("e").trim();
            if (idxOptions.containsKey("c"))
                compressing = idxOptions.get("c").trim();
        }
        indexFolderName = getName() + "_b." + binning + "_e." + encoding;
        strIdxOpt = "\"<binning " + binning + "/><encoding " + encoding + "/>";
        if (compressing != null) {
            strIdxOpt += "<compressing " + compressing + "/>";
            indexFolderName += "_c." + compressing;
        }
        strIdxOpt += "\"";
    }
}