package ghost.xapi.statements.feedback;

import ghost.xapi.entities.Statement;
import ghost.xapi.statements.StatementBuilder;
import ghost.xapi.statements.StatementBuilderBlock;
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

	private StatementBuilderBlock block = new StatementBuilderBlock() {
		@Override
		public Statement create(String requestUri, HttpServletRequest request) {
			if (requestUri.contains("mycourses")) {
				return courseStatementBuilderService.buildForGetCourses(request);
			}

			return null;
		}

		@Override
		public Statement createWithIoOperations(String requestUri, HttpServletRequest request) throws IOException {
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
