package project.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static project.test.Constants.srcTestResourcesDir;
import static java.lang.System.lineSeparator;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.lang3.CharEncoding.UTF_8;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.difference;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class EntityTestBase<E, T> {

    protected ObjectMapper mapper = new ObjectMapper();
    protected Class<E> entityClass;
    protected String entityClassName;
    protected T test;
    protected String directoryName;
    protected File directory;
    protected Collection<File> files;
    protected List<String> fileNames = new ArrayList<>();
    protected List<String> jsons = new ArrayList<>();
    protected List<E> entities = new ArrayList<>();
    protected File currentFile;
    protected String currentFileName;
    protected String currentFileBaseName;
    protected String currentJson;
    protected E currentEntity;
    protected String currentMappedJson;
    protected String callbackName;
    protected ArrayList array;
    protected HashMap map;

    public void validate() throws Exception {
        entityClassName = entityClass.getSimpleName();
        directoryName = entityClassName.toLowerCase();
        directory = openTestFile("/entities/" + directoryName);
        files = listFiles(directory, new String[] { "json" }, false);
        for (File file : files) {
            currentFile = file;
            currentFileName = file.getName();
            currentFileBaseName = getBaseName(currentFileName);
            currentJson = readFileToString(file, UTF_8);
            currentEntity = mapper.readValue(currentJson, entityClass);
            currentMappedJson = mapper.writeValueAsString(currentEntity);
            fileNames.add(currentFileName);
            jsons.add(currentJson);
            entities.add(currentEntity);
            checkJsonEquals(currentJson, currentMappedJson);
            checkEntity();
        }
        assert true;
    }

    protected void checkEntity() throws Exception {
        callbackName = "check"
                + currentFileBaseName.replace(entityClassName, EMPTY);
        Method[] methods = test.getClass().getMethods();
        for (Method method : methods) {
            runCheckMethod(method);
        }
    }

    protected void runCheckMethod(Method method) throws Exception {
        String methodName = method.getName();
        if (methodName.equals(callbackName)) {
            method.invoke(test);
            System.out.println("[Done] " + callbackName);
        }
    }

    protected boolean isTestFileSufix(String sufix) {
        return currentFileName.equals(testFile(sufix));
    }

    protected String testFile(String sufix) {
        return entityClass.getSimpleName() + sufix + ".json";
    }

    protected void setArray(Object object) {
        array = ArrayList.class.cast(object);
    }

    protected void setMap(Object object) {
        if (object instanceof LinkedHashMap) {
            map = LinkedHashMap.class.cast(object);
        }
        if (object instanceof HashMap) {
            map = HashMap.class.cast(object);
        }
    }

    protected void checkJsonEqualsFile(String jsonExpected, String testFile)
            throws Exception {
        File file = openTestFile(testFile);
        String json = readFileToString(file, UTF_8);
        checkJsonEquals(jsonExpected, json);
    }

    protected void checkJsonEquals(String jsonExpected, String json)
            throws Exception {
        JsonNode nodeExpected = mapper.readTree(jsonExpected);
        JsonNode node = mapper.readTree(json);
        boolean equals = nodeExpected.equals(node);
        if (!equals) {
            printDifferences(jsonExpected, json);
        }
        assertTrue(equals);
    }

    protected void printDifferences(String jsonExpected, String json)
            throws Exception {
        jsonExpected = formatJson(jsonExpected);
        json = formatJson(json);
        String diff = difference(jsonExpected, json);
        System.out.println("- Expected Json:" + lineSeparator() + jsonExpected);
        System.out.println("- Actual Json:" + lineSeparator() + json);
        System.out.println("- Different from: " + lineSeparator() + diff);
    }

    protected String formatJson(String json) throws Exception {
        JsonNode node = mapper.readTree(json);
        return mapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(node);
    }

    protected File openTestFile(String testFile) {
        return new File(srcTestResourcesDir + "/" + testFile);
    }

}
