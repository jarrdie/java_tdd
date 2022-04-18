package project.core.usecases.file;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.InputStream;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public interface CreateFile {

    public class Request {

        public String source = EMPTY;
        public String basePath = EMPTY;
        public String path = EMPTY;
        public InputStream input;
        public String permissions = EMPTY;

    }

    public class Response {

        public boolean error = false;
        public ObjectNode node;

    }

    public Response createFile(Request request) throws Exception;

}
