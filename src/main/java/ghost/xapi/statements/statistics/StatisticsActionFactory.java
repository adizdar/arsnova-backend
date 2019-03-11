package ghost.xapi.statements.statistics;

import ghost.xapi.entities.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Component
public class StatisticsActionFactory {

	private AntPathMatcher patchMatcher = new AntPathMatcher();

	@Autowired
	private StatisticsStatementBuilderService userStatementBuilderService;

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement getStatementViaServiceName(HttpServletRequest request) throws IOException {
		String requestUri = request.getRequestURI().toLowerCase();

		if (this.doesUriMatchWithPattern(request, "/statistics")
				|| this.doesUriMatchWithPattern(request, "/statistics/")) {
			return this.userStatementBuilderService.buildForGetStatistics(request);
		} else if (requestUri.contains("activeusercount")) {
			return this.userStatementBuilderService.buildForGetActiveUserCount(request);
		} else if (requestUri.contains("loggedinusercount")) {
			return this.userStatementBuilderService.buildForGetLogginUserCount(request);
		} else if (requestUri.contains("sessioncount")) {
			return this.userStatementBuilderService.buildForGetSessionCount(request);
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
