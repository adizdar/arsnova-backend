package ghost.xapi.statements.socket;

import ghost.xapi.entities.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class SocketActionFactory {

	@Autowired
	private SocketStatementBuilderService socketStatementBuilderService;

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement getStatementViaServiceName(HttpServletRequest request) throws IOException {
		String requestUri = request.getRequestURI().toLowerCase();

		if (requestUri.contains("assign")) {
			return this.socketStatementBuilderService.buildForAuthorizeSocketAssign(request);
		} else if (requestUri.contains("url")) {
			return this.socketStatementBuilderService.buildForGetSocketUrl(request);
		}

		// This case should only happen if ARSNOVA registers a new action or we don't support the action
		// TODO custom exception
		throw new NullPointerException();
	}

}
