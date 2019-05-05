package ghost.xapi.statements.user;

import ghost.xapi.entities.Statement;
import ghost.xapi.statements.StatementBuilder;
import ghost.xapi.statements.StatementBuilderBlock;
import ghost.xapi.statements.UriMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class UserActionFactory {

	@Autowired
	private UserStatementBuilderService userStatementBuilderService;

	private StatementBuilderBlock block = new StatementBuilderBlock() {
		@Override
		public Statement create(String requestUri, HttpServletRequest request) {
			if (requestUri.contains("register")) {
				return userStatementBuilderService.buildForRegisterUser(request);
			} else if (requestUri.contains("activate")) {
				return userStatementBuilderService.buildForActivateUser(request);
			} else if (requestUri.contains("resetpassword")) {
				return userStatementBuilderService.buildForResetPassword(request);
			} else if (UriMatchService.doesUriMatchWithPattern(request, "/{username}/")
					|| UriMatchService.doesUriMatchWithPattern(request, "/{username}")) {
				return userStatementBuilderService.buildForDeleteUser(request);
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
	 *
	 * @return Statement
	 */
	public Statement getStatementViaServiceName(HttpServletRequest request) {
		return StatementBuilder.createFromRequest(request, this.block);
	}

}
