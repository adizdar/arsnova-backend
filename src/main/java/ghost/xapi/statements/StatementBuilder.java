package ghost.xapi.statements;

import ghost.xapi.entities.Statement;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class StatementBuilder {

	/**
	 * @param request
	 * @param statementBuilderBlock
	 * @return Statement
	 */
	public static Statement createFromRequest(HttpServletRequest request, StatementBuilderBlock statementBuilderBlock) {
		String requestUri = request.getRequestURI().toLowerCase();

		Statement statement = statementBuilderBlock.create(requestUri, request);
		if (statement == null) {
			// This case should only happen if ARSNOVA registers a new action.
			throw new NullPointerException("Action for URI " + requestUri + " is not defined.");
		}

		return statement;
	}

	/**
	 * @param request
	 * @param statementBuilderBlock
	 * @return Statement
	 * @throws IOException
	 */
	public static Statement createFromRequestWithIOOperations(HttpServletRequest request, StatementBuilderBlock statementBuilderBlock) throws IOException {
		String requestUri = request.getRequestURI().toLowerCase();

		Statement statement = statementBuilderBlock.createWithIoOperations(requestUri, request);
		if (statement == null) {
			// This case should only happen if ARSNOVA registers a new action.
			throw new NullPointerException("Action for URI " + requestUri + " is not defined.");
		}

		return statement;
	}

}
