package project.test;

public class Constants {

    public static final String eol = System.lineSeparator();
    public static final String tab = "\t";
    public static final String cwd = System.getProperty("user.dir").replaceAll("\\\\", "/");
    public static final String tmpDir = System.getProperty("java.io.tmpdir");
    public static final String workingDir = System.getProperty("user.dir");
    public static final String srcMainDir = workingDir + "/src/main/java";
    public static final String srcTestDir = workingDir + "/src/test/java";
    public static final String srcTestResourcesDir = workingDir + "/src/test/resources";
    public static final String srcMainResourcesDir = workingDir + "/src/main/resources";

}
