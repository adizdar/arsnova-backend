package ghost.xapi.statements;

import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

public class UriMatchService {
	/**
	 * @param request
	 * @param uriToMatch
	 * @return
	 */
	public static boolean doesUriMatchWithPattern(HttpServletRequest request, String uriToMatch) {
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

		return bestMatchPattern.equals(uriToMatch);
	}
}
