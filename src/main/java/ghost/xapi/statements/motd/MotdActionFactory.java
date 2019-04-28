package ghost.xapi.statements.motd;

import de.thm.arsnova.services.IUserService;
import ghost.xapi.entities.Statement;
import ghost.xapi.statements.StatementBuilder;
import ghost.xapi.statements.StatementBuilderBlock;
import ghost.xapi.statements.UriMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class MotdActionFactory {

	@Autowired
	private MotdStatementBuilderService motdStatementBuilderService;

	@Autowired
	private IUserService userService;

	private StatementBuilderBlock block = new StatementBuilderBlock() {
		@Override
		public Statement create(String requestUri, HttpServletRequest request) {
			// Only placeholder.
			return null;
		}

		@Override
		public Statement createWithIoOperations(String requestUri, HttpServletRequest request) throws IOException {
			String requestMethod = request.getMethod().toLowerCase();

			if (UriMatchService.doesUriMatchWithPattern(request, "/motd")
					|| UriMatchService.doesUriMatchWithPattern(request, "/motd/")) {
				switch (requestMethod) {
					case "post":
						return motdStatementBuilderService.buildForPostNewtMotd(request);
					case "get":
						return motdStatementBuilderService.buildForGetMotd(request);
				}
			} else if (UriMatchService.doesUriMatchWithPattern(request, "/motd/{motdkey}/")
					|| UriMatchService.doesUriMatchWithPattern(request, "/motd/{motdkey}")) {
				switch (requestMethod) {
					case "put":
						return motdStatementBuilderService.buildForUpdateMotd(request);
					case "delete":
						return motdStatementBuilderService.buildForDeleteMotd(request);
				}
			} else if (requestUri.contains("userlist")) {
				switch (requestMethod) {
					case "get":
						return motdStatementBuilderService.buildForGetUserMotdList(request);
					case "post":
						return motdStatementBuilderService.buildForPostUserMotdList(request);
					case "put":
						return motdStatementBuilderService.buildForUpdateUserMotdList(request);
				}
			}
			
			return null;
		}
	};

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement getStatementViaServiceName(HttpServletRequest request) throws IOException {
		Statement statement = StatementBuilder.createFromRequestWithIOOperations(request, this.block);
		statement.addUserRoleToContext(this.userService.getCurrentUser());

		return statement;
	}

}
