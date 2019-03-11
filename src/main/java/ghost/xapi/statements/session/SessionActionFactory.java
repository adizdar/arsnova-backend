package ghost.xapi.statements.session;

import ghost.xapi.entities.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Component
public class SessionActionFactory {

	private AntPathMatcher patchMatcher = new AntPathMatcher();

	@Autowired
	private SessionStatementBuilderService sessionStatementBuilderService;

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement getStatementViaServiceName(HttpServletRequest request) throws IOException {
		Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String requestUri = request.getRequestURI().toLowerCase();

		if (this.doesUriMatchWithPattern(request, "/session/{sessionkey}")
				|| this.doesUriMatchWithPattern(request, "/session/{sessionkey}/")) {
			String methodName = request.getMethod().toLowerCase();
			switch (methodName) {
				case "delete":
					return this.sessionStatementBuilderService.buildForDeleteSession(request, pathVariables);
				case "put":
					return this.sessionStatementBuilderService.buildForUpdateSession(request, pathVariables);
			}
		} else if (this.doesUriMatchWithPattern(request, "/session")
				|| this.doesUriMatchWithPattern(request, "/session/")) {
			String methodName = request.getMethod().toLowerCase();
			switch (methodName) {
				case "post":
					return this.sessionStatementBuilderService.buildForPostNewSession(request);
				case "get":
					if (request.getParameterMap().containsKey("ownedOnly")) {
						return this.sessionStatementBuilderService.buildForGetSessions(request);
					}

					return this.sessionStatementBuilderService.buildForGetMySession(request);
			}
		} else if (requestUri.contains("publicpool")) {
			return this.sessionStatementBuilderService.buildForGetMyPublicPoolSessions(request);
		} else if (requestUri.contains("import")) {
			return this.sessionStatementBuilderService.buildForImportSession(request);
		} else if (requestUri.contains("export")) {
			return this.sessionStatementBuilderService.buildForExportSession(request);
		} else if (requestUri.contains("changecreator")) {
			return this.sessionStatementBuilderService.buildForUpdateSessionCreator(request, pathVariables);
		} else if (requestUri.contains("copytopublicpool")) {
			return this.sessionStatementBuilderService.buildForCopyToPublicPool(request, pathVariables);
		} else if (this.doesUriMatchWithPattern(request, "/session/{sessionkey}/lock")
				|| this.doesUriMatchWithPattern(request, "/session/{sessionkey}/lock/")) {
			return this.sessionStatementBuilderService.buildForLockSession(request, pathVariables);
		} else if (requestUri.contains("learningprogress")) {
			return this.sessionStatementBuilderService.buildGetLearningProgress(request, pathVariables);
		} else if (requestUri.contains("features")) {
			return this.sessionStatementBuilderService.buildForGetSessionFeatures(request, pathVariables);
		} else if (requestUri.contains("lockfeedbackinput")) {
			return this.sessionStatementBuilderService.buildForLockFeedbackInput(request, pathVariables);
		} else if (requestUri.contains("flipflashcards")) {
			return this.sessionStatementBuilderService.buildForFlipFlashcards(request, pathVariables);
		}

		// This case should only happen if ARSNOVA registers a new action or we don't support the action
		// TODO custom exception
		throw new NullPointerException();
	}

	protected boolean doesUriContainsPattern(HttpServletRequest request, String uriToMatch) {
		String bestMatchPattern = (String ) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

		return bestMatchPattern.contains(uriToMatch);
	}

	protected boolean doesUriMatchWithPattern(HttpServletRequest request, String uriToMatch) {
		String bestMatchPattern = (String ) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

		return bestMatchPattern.equals(uriToMatch);
	}

}
