package project.core.usecases.file;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.InputStream;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public interface UpdateFile {

    public class Request {

        public String source = EMPTY;
        public String basePath = EMPTY;
        public String path = EMPTY;
        public InputStream input;
        public boolean validate = true;
        public String mimeType = EMPTY;
        public boolean useServerEol = false;
        public String eol = EMPTY;

    }

    public class Response {

        public boolean error = false;
        public ObjectNode node;

    }

    public Response updateFile(Request request) throws IOException;

}
