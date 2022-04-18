package project.core.entities.encoding;

import project.core.entities.file.InputStreamConverter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class EndOfLine {

    public static void unifyLineBreaksToUnix(File file) {
        try {
            String content = readAsUtf8(file);
            content = content.replace("\r\n", "\n");
            content = content.replace("\r", "\n");
            writeFromUtf8(content, file);
        } catch (Exception exception) {
            System.out.println("Error unifying the line breaks to Unix");
        }
    }

    public static String readAsUtf8(File file) throws Exception {
        StringBuffer buffer = new StringBuffer();
        FileInputStream input = new FileInputStream(file);
        InputStreamReader inputUtf8 = new InputStreamReader(input, "UTF8");
        Reader reader = new BufferedReader(inputUtf8);
        int character;
        while ((character = reader.read()) > -1) {
            buffer.append((char) character);
        }
        reader.close();
        return buffer.toString();
    }

    public static void writeFromUtf8(String content, File file) throws Exception {
        FileOutputStream output = new FileOutputStream(file);
        Writer writer = new OutputStreamWriter(output, "UTF8");
        writer.write(content);
        writer.close();
    }

    public static InputStream addLineBreakConverter(InputStream input, String from, String to) {
        if (isEmpty(to)) {
            to = lineSeparator();
        }
        return new InputStreamConverter(input, from.getBytes(), to.getBytes());
    }

}
