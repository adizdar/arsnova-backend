package ghost.xapi.statements.authentication;

import ghost.xapi.entities.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthenticationActionFactory {

	@Autowired
	private AuthenticationStatementBuilderService authenticationStatementBuilderService;

	/**
	 * @param request
	 * @return Statement
	 */
	public Statement getStatementViaServiceName(HttpServletRequest request) {
		String requestUri = request.getRequestURI().toLowerCase();
		if (requestUri.contains("login") || requestUri.contains("dologin")) {
			return this.authenticationStatementBuilderService.buildForLoginAction(request);
		} else if (requestUri.contains("dialog")) {
			return this.authenticationStatementBuilderService.buildForGetAuthDialog(request);
		} else if (requestUri.contains("logout")) {
			return this.authenticationStatementBuilderService.buildForLogoutAction(request);
		} else if (requestUri.contains("whoami") || requestUri.contains("auth")) {
			return this.authenticationStatementBuilderService.buildForGetUserInformation(request);
		}

		// This case should only happen if ARSNOVA registers a new action
		// TODO custom exception
		throw new NullPointerException();
	}

}
