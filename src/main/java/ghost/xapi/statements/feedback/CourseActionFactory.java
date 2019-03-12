package ghost.xapi.statements.feedback;

import ghost.xapi.entities.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class CourseActionFactory {

	@Autowired
	private CourseStatementBuilderService courseStatementBuilderService;

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement getStatementViaServiceName(HttpServletRequest request) {
		String requestUri = request.getRequestURI().toLowerCase();

		if (requestUri.contains("mycourses")) {
			return this.courseStatementBuilderService.buildForGetCourses(request);
		}

		// This case should only happen if ARSNOVA registers a new action or we don't support the action
		// TODO custom exception
		throw new NullPointerException();
	}

}
