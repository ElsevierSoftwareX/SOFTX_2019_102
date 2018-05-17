/*
 Copyright (c) 2017 Thaylon Guedes Santos

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 */
package indexer;

import enumeration.indexer.AttributeType;
import enumeration.indexer.ExtensionType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import rdi.utils.Utils;

/**
 * @author Thaylon Guedes Santos
 * @email thaylongs@gmail.com
 */
public class PostgresRAWIndexer extends Indexer {

    private BufferedWriter obw = null;
    private String postgresRAWbin;
    private String pgDataDir;
    private List<String> existingTablesNames = new ArrayList<>();
    private File indexFile;
    private File postgresRAWConfFile;
    private int lastFileCont = 0;
    private boolean postgresAlreadyRunning = false;
    private boolean hashHeader = false;

    public PostgresRAWIndexer(String indexerName) {
        super(ExtensionType.POSTGRES_RAW, indexerName);
    }

    /**
     * Only for tests
     *
     * @param postgresRAWbin
     * @param pgDataDir
     */
    public PostgresRAWIndexer(String indexerName, String postgresRAWbin, String pgDataDir) {
        super(ExtensionType.POSTGRES_RAW, indexerName);
        this.postgresRAWbin = postgresRAWbin;
        this.pgDataDir = pgDataDir;
    }

    @Override
    public String run(String path, String fileName, String[] attributeMappings, ArrayList<String> keys) {
        try {
            setAttributeMappings(attributeMappings);
            HashMap<String, AttributeType> attributes = getAttributes(attributeMappings);
            
            postgresRAWbin = binaryDir;
            pgDataDir = getPgData();
            checkBinaryDirInfo();

            String[] strFiles = Utils.getFiles(path, fileName);
            createPGDataDirIfNotExists();
            postgresRAWConfFile = getPostgresRAWConfFile();
            postgresAlreadyRunning = postgresRAWisRunning();
            if (!postgresAlreadyRunning) {
                startPostgresRAW();
                System.out.println("Waiting for database start");
                Thread.sleep(2000);
            }
            lastFileCont = getLastFileCount(postgresRAWConfFile);
            if (idxOptions != null && !idxOptions.isEmpty()) {
                if (idxOptions.containsKey("header")) {
                    if (idxOptions.get("header").equals("true")) {
                        hashHeader = true;
                    }
                }
            }

            indexFile = new File(path + getIndexerFile());
            indexFile.createNewFile();
            obw = new BufferedWriter(new FileWriter(indexFile));
            obw.write("FILENAME;TABLENAME");
            for (String filename : strFiles) {
                index(path, filename, attributes, keys);
            }
            obw.close();
            return path + getIndexerFile();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (!postgresAlreadyRunning) {
                try {
                    stopPostgresRAW();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     *
     * @param path to file dir
     * @param fileName the name of file test1.csv
     * @param attributes the columns of file
     * @param keys 
     */
    @Override
    public void index(String path, String fileName, HashMap<String, AttributeType> attributes, ArrayList<String> keys) {
        try {
            int currentFileCount = ++lastFileCont;

            String databaseName = getName().replaceAll("[^a-zA-Z0-9\\_]+", "");
            String tableName = fileName.replaceAll("[^a-zA-Z0-9\\_]+", "");
            tableName = databaseName + "_" + tableName;

            if (existingTablesNames.contains(tableName)) {
                throw new RuntimeException(new Exception("The table name already exist in the postgresRAW configuration file"));
            }

            writeNewEntryOnPostgresRAW(postgresRAWConfFile, Paths.get(path, fileName), currentFileCount, tableName, delimiter, hashHeader);
            createDataBaseIfNotExists(databaseName);
            createTable(databaseName, tableName, attributes);
            obw.write(String.format("\n'%s';'%s'",                    
                    Paths.get(pgDataDir).toAbsolutePath().toString(),                 
                    tableName)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param filePath ...DataflowAnalyzer/RawDataIndexer/
     * @param fileName test1.csv
     * @param valueMappings [A, B]
     * @param keys []
     * @return
     */
    @Override
    public String access(String filePath, String fileName, HashMap<String, String> valueMappings, ArrayList<String> keys) {
        postgresRAWbin = binaryDir;
        checkBinaryDirInfo();
        try {

            String databaseName = getName().replaceAll("[^a-zA-Z0-9\\_]+", "");
            String tableName = fileName.replaceAll("[^a-zA-Z0-9\\_]+", "");
            tableName = databaseName + "_" + tableName;

            indexFile = Paths.get(filePath, getIndexerFile()).toFile();

            pgDataDir = getPGDataDirFromIndexFile(indexFile, tableName);
            if (pgDataDir == null) {
                throw new Exception("The " + fileName + " was not found in the index file: " + indexFile);
            }
            postgresAlreadyRunning = postgresRAWisRunning();
            if (!postgresAlreadyRunning) {
                startPostgresRAW();
                System.out.println("Waiting for database start");
                Thread.sleep(2000);
            }
            Path acessPath = Paths.get(filePath, getAccessFile()).toAbsolutePath();
            //\copy (select ...) to '~/myfile.csv' WITH (FORMAT CSV, HEADER TRUE)
            String sql = "\\copy ( select ";
            if (valueMappings.isEmpty()) {
                sql += " * ";
            } else {
                for (String column : valueMappings.keySet()) {
                    sql += "\\\"" + column + "\\\", ";
                }
                sql = sql.substring(0, sql.length() - 2);
            }
            sql += " from \\\"" + tableName + "\\\") to '" + acessPath.toString() + "' WITH (FORMAT CSV, HEADER TRUE, DELIMITER ';')";            
            executeQuery(databaseName, sql);

            return "The output data is in the file: " + getAccessFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (!postgresAlreadyRunning) {
                try {
                    stopPostgresRAW();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getPGDataDirFromIndexFile(File indexFIle, String tableName) throws IOException {
        tableName = ("'" + tableName + "'").trim();
        BufferedReader br = new BufferedReader(new FileReader(indexFile));
        br.readLine();//Header
        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split(";");
            if (data[data.length - 1].trim().equals(tableName)) {
                return data[0].replaceAll("(\\')", "");
            }
        }
        return null;
    }

    private void checkBinaryDirInfo() {
        File f = Paths.get(postgresRAWbin, "pg_ctl").toFile();
        if (!f.exists()) {
            throw new RuntimeException(new Exception("The postgres raw bin direcory was be informated no exists: '" + f + "'"));
        }
    }

    private int getLastFileCount(File postgresRAWConfFile) throws IOException {
        if (postgresRAWConfFile.length() == 0) {
            return 0;
        }
        BufferedReader br = new BufferedReader(new FileReader(postgresRAWConfFile));
        int lastCount = 0;
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("relation")) {
                //relation-1 = 'persons'
                String number = line.split("=")[0].trim().split("-")[1].trim();
                lastCount = Integer.parseInt(number);
                String tableName = line.split("=")[1].trim().replace("'", "").replace("'", "").trim();
                existingTablesNames.add(tableName);
            }
        }
        return lastCount;
    }

    private File getPostgresRAWConfFile() throws IOException {
        File confFile = Paths.get(pgDataDir, "postgresql.conf").toFile();
        BufferedReader br = new BufferedReader(new FileReader(confFile));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("conf_file")) {
                String fileName = line.split("=")[1];
                fileName = fileName.replace("'", "").replace("'", "").trim();
                File rawConfFile = Paths.get(pgDataDir, fileName).toFile();
                if (!rawConfFile.exists()) {
                    rawConfFile.createNewFile();
                }
                return rawConfFile;
            }
        }
        return null;
    }

    public void createPGDataDirIfNotExists() throws IOException, InterruptedException {
        File pgData = new File(pgDataDir);
        if (!pgData.exists()) {
            pgData.mkdirs();
        }
        if (!new File(pgData, "postgresql.conf").exists()) {
            createPGDataDir();
        }
    }

    private void createDataBaseIfNotExists(String dataBaseName) throws IOException, InterruptedException {
        boolean existDataBase = false;
        Object[] out = Utils.runCommandAndReadOutput("./psql -l", postgresRAWbin, verbose);
        for (String line : ((String) out[1]).split("\n")) {
            String[] data = line.split("|");
            String existingDBName = data[0].trim();
            if (existingDBName.equals(dataBaseName)) {
                existDataBase = true;
                break;
            }
        }
        if (!existDataBase) {
            Utils.runCommand("./createdb '" + dataBaseName + "'", postgresRAWbin, verbose);
        }
    }

    public int createPGDataDir() throws IOException, InterruptedException {
        return Utils.runCommand("./initdb -D '" + pgDataDir + "'", postgresRAWbin, verbose);
    }

    public int stopPostgresRAW() throws IOException, InterruptedException {
        return Utils.runCommand("./pg_ctl stop -m fast -D '" + pgDataDir + "'", postgresRAWbin, verbose);
    }

    public int startPostgresRAW() throws IOException, InterruptedException {
        return Utils.runCommand("./pg_ctl start -D '" + pgDataDir + "'", postgresRAWbin, verbose);
    }

    public boolean postgresRAWisRunning() throws IOException, InterruptedException {
        return Utils.runCommand("./pg_ctl status -D '" + pgDataDir + "'", postgresRAWbin, verbose) == 0;
    }

    private void writeNewEntryOnPostgresRAW(File postgresRAWConfFile, Path path, int currentFileCount, String tableName, String delimiter, boolean hasHeader) throws IOException {
        String template
                = "\nfilename-" + currentFileCount + " = '" + path.toAbsolutePath().toString() + "'\n"
                + "relation-" + currentFileCount + " = '" + tableName + "'\n"
                + "delimiter-" + currentFileCount + " = '" + delimiter + "'\n";
        if (hasHeader) {
            template += "header-" + currentFileCount + " = 'True'\n";
        }
        Files.write(postgresRAWConfFile.toPath(), template.getBytes(), StandardOpenOption.APPEND);
    }

    private void createTable(String dataBase, String tableName, HashMap<String, AttributeType> attributes) throws IOException, InterruptedException {
        StringBuilder stringBuilder = new StringBuilder("create table \\\"" + tableName + "\\\"( ");
        int columnIndex = 0;
        for (Entry<String, AttributeType> column : attributes.entrySet()) {
            String name = column.getKey();
            AttributeType type = column.getValue();
            stringBuilder.append("\\\"").append(name).append("\\\" ");
            switch (type) {
                case TEXT:
                case FILE:
                    stringBuilder.append("text, ");
                    break;
                case NUMERIC:
                    String[] columnData = getAttributeMappings()[columnIndex++].split(":");
                    int precision = AttributeType.getPrecisionOfNumber(columnData);
                    if (precision <= 0) {
                        stringBuilder.append("integer, ");
                    } else if (precision <= 6) {
                        stringBuilder.append("real, ");
                    } else {
                        stringBuilder.append("double precision, ");
                    }
                    break;
            }
        }
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length() - 1);
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(");");
        String query = stringBuilder.toString();
        executeQuery(dataBase, query);
    }

    private void executeQuery(String database, String query) throws IOException, InterruptedException {
        Utils.runCommand("./psql -d '" + database + "' -c \"" + query + "\"", postgresRAWbin, verbose);
    }
}