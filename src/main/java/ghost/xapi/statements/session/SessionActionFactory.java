package ghost.xapi.statements.session;

import ghost.xapi.entities.Statement;
import ghost.xapi.statements.StatementBuilder;
import ghost.xapi.statements.StatementBuilderBlock;
import ghost.xapi.statements.UriMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Component
public class SessionActionFactory {

	@Autowired
	private SessionStatementBuilderService sessionStatementBuilderService;

	private StatementBuilderBlock block = new StatementBuilderBlock() {
		@Override
		public Statement create(String requestUri, HttpServletRequest request) {
			// Only placeholder.
			return null;
		}

		@Override
		public Statement createWithIoOperations(String requestUri, HttpServletRequest request) throws IOException {
			Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

			if (UriMatchService.doesUriMatchWithPattern(request, "/session/{sessionkey}")
					|| UriMatchService.doesUriMatchWithPattern(request, "/session/{sessionkey}/")) {
				String methodName = request.getMethod().toLowerCase();
				switch (methodName) {
					case "delete":
						return sessionStatementBuilderService.buildForDeleteSession(request, pathVariables);
					case "put":
						return sessionStatementBuilderService.buildForUpdateSession(request, pathVariables);
				}
			} else if (UriMatchService.doesUriMatchWithPattern(request, "/session")
					|| UriMatchService.doesUriMatchWithPattern(request, "/session/")) {
				String methodName = request.getMethod().toLowerCase();
				switch (methodName) {
					case "post":
						return sessionStatementBuilderService.buildForPostNewSession(request);
					case "get":
						if (request.getParameterMap().containsKey("ownedOnly")) {
							return sessionStatementBuilderService.buildForGetSessions(request);
						}

						return sessionStatementBuilderService.buildForGetMySession(request);
				}
			} else if (requestUri.contains("publicpool")) {
				return sessionStatementBuilderService.buildForGetMyPublicPoolSessions(request);
			} else if (requestUri.contains("import")) {
				return sessionStatementBuilderService.buildForImportSession(request);
			} else if (requestUri.contains("export")) {
				return sessionStatementBuilderService.buildForExportSession(request);
			} else if (requestUri.contains("changecreator")) {
				return sessionStatementBuilderService.buildForUpdateSessionCreator(request, pathVariables);
			} else if (requestUri.contains("copytopublicpool")) {
				return sessionStatementBuilderService.buildForCopyToPublicPool(request, pathVariables);
			} else if (UriMatchService.doesUriMatchWithPattern(request, "/session/{sessionkey}/lock")
					|| UriMatchService.doesUriMatchWithPattern(request, "/session/{sessionkey}/lock/")) {
				return sessionStatementBuilderService.buildForLockSession(request, pathVariables);
			} else if (requestUri.contains("learningprogress")) {
				return sessionStatementBuilderService.buildGetLearningProgress(request, pathVariables);
			} else if (requestUri.contains("features")) {
				return sessionStatementBuilderService.buildForGetSessionFeatures(request, pathVariables);
			} else if (requestUri.contains("lockfeedbackinput")) {
				return sessionStatementBuilderService.buildForLockFeedbackInput(request, pathVariables);
			} else if (requestUri.contains("flipflashcards")) {
				return sessionStatementBuilderService.buildForFlipFlashcards(request, pathVariables);
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
		return StatementBuilder.createFromRequestWithIOOperations(request, this.block);
	}

}
