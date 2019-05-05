package ghost.xapi.statements;

import ghost.xapi.entities.Statement;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface StatementBuilderBlock {
	Statement create(String requestUri, HttpServletRequest request);
	Statement createWithIoOperations(String requestUri, HttpServletRequest request) throws IOException;
}
