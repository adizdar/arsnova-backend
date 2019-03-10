package ghost.xapi.statements.authentication;

import ghost.xapi.entities.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class LoginActionFactory {

	@Autowired
	private LoginStatementBuilderService loginStatementBuilderService;

	/**
	 * @param request
	 * @return Statement
	 */
	public Statement getStatementViaServiceName(HttpServletRequest request) {
		String requestUri = request.getRequestURI().toLowerCase();
		if (requestUri.contains("login") || requestUri.contains("dologin")) {
			return this.loginStatementBuilderService.buildForLoginAction(request);
		} else if (requestUri.contains("logout")) {
			return this.loginStatementBuilderService.buildForLogoutAction(request);
		}

		// This case should only happen if ARSNOVA registers a new action
		// TODO custom exception
		throw new NullPointerException();
	}

}
