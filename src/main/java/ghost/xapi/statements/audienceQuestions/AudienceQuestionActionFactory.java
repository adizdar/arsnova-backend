package ghost.xapi.statements.audienceQuestions;

import de.thm.arsnova.entities.User;
import de.thm.arsnova.services.IUserService;
import ghost.xapi.entities.Context;
import ghost.xapi.entities.Statement;
import ghost.xapi.statements.StatementBuilder;
import ghost.xapi.statements.StatementBuilderBlock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Component
public class AudienceQuestionActionFactory {

	@Autowired
	private AudienceQuestionStatementBuilderService audienceQuestionStatmentBuilderService;

	@Autowired
	private IUserService userService;

	private StatementBuilderBlock block = new StatementBuilderBlock() {
		@Override
		public Statement create(String requestUri, HttpServletRequest request) {
			if(requestUri.contains("readcount")) {
				return audienceQuestionStatmentBuilderService.buildForUnreadInterposedQuestionsCount(request);
			} else if (requestUri.contains("audiencequestion")) {
				String methodName = request.getMethod().toLowerCase();
				switch (methodName) {
					case "post":
						return audienceQuestionStatmentBuilderService.buildForAskedInterposedQuestion(request);
					case "get":
						Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
						if (pathVariables.isEmpty()) {
							return audienceQuestionStatmentBuilderService.buildForGetInterposedQuestion(request);
						}

						return audienceQuestionStatmentBuilderService.buildForGetOneInterposedQuestion(pathVariables);
					case "delete":
						return audienceQuestionStatmentBuilderService.buildForDeleteInterposedQuestion(request);
				}
			}

			return null;
		}

		@Override
		public Statement createWithIoOperations(String requestUri, HttpServletRequest request) {
			// Only placeholder.
			return null;
		}
	};

	/**
	 * @param request
	 * @return Statement
	 */
	public Statement getStatementViaServiceName(HttpServletRequest request) {
		Statement statement = StatementBuilder.createFromRequest(request, this.block);
		statement.addUserRoleToContext(this.userService.getCurrentUser());

		return statement;
	}

}
