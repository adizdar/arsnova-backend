package ghost.xapi.statements.feedback;

import ghost.xapi.entities.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class FeedbackActionFactory {

	private AntPathMatcher patchMatcher = new AntPathMatcher();

	@Autowired
	private FeedbackStatementBuilderService feedbackStatementBuilderService;

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement getStatementViaServiceName(HttpServletRequest request) throws IOException {
		String requestUri = request.getRequestURI().toLowerCase();

		if (this.doesUriMatchWithPattern(request, "/statistics")
				|| this.doesUriMatchWithPattern(request, "/statistics/")) {
			return this.feedbackStatementBuilderService.buildForGetStatistics(request);
		} else if (requestUri.contains("activeusercount")) {
			return this.feedbackStatementBuilderService.buildForGetActiveUserCount(request);
		} else if (requestUri.contains("loggedinusercount")) {
			return this.feedbackStatementBuilderService.buildForGetLogginUserCount(request);
		} else if (requestUri.contains("sessioncount")) {
			return this.feedbackStatementBuilderService.buildForGetSessionCount(request);
		}

		// This case should only happen if ARSNOVA registers a new action or we don't support the action
		// TODO custom exception
		throw new NullPointerException();
	}


	/**
	 * TODO move to abstract class
	 *
	 * @param request
	 * @param uriToMatch
	 *
	 * @return
	 */
	protected boolean doesUriMatchWithPattern(HttpServletRequest request, String uriToMatch) {
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

		return bestMatchPattern.equals(uriToMatch);
	}

}
