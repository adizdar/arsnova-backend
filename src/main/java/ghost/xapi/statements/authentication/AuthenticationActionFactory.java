package ghost.xapi.statements.authentication;

import ghost.xapi.entities.Statement;
import ghost.xapi.statements.StatementBuilder;
import ghost.xapi.statements.StatementBuilderBlock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class AuthenticationActionFactory {

	@Autowired
	private AuthenticationStatementBuilderService authenticationStatementBuilderService;

	private StatementBuilderBlock block = new StatementBuilderBlock() {
		@Override
		public Statement create(String requestUri, HttpServletRequest request) {
			if (requestUri.contains("login") || requestUri.contains("dologin")) {
				return authenticationStatementBuilderService.buildForLoginAction(request);
			} else if (requestUri.contains("dialog")) {
				return authenticationStatementBuilderService.buildForGetAuthDialog(request);
			} else if (requestUri.contains("logout")) {
				return authenticationStatementBuilderService.buildForLogoutAction(request);
			} else if (requestUri.contains("whoami") || requestUri.contains("auth")) {
				return authenticationStatementBuilderService.buildForGetUserInformation(request);
			}

			return null;
		}

		@Override
		public Statement createWithIoOperations(String requestUri, HttpServletRequest request) throws IOException {
			// Only placeholder.
			return null;
		}
	};

	/**
	 * @param request
	 * @return Statement
	 */
	public Statement getStatementViaServiceName(HttpServletRequest request) {
		return StatementBuilder.createFromRequest(request, this.block);
	}

}
