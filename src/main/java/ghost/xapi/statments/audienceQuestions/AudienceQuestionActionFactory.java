package ghost.xapi.statments.audienceQuestions;

import ghost.xapi.entities.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
public class AudienceQuestionActionFactory {

	@Autowired
	private AudienceQuestionStatementBuilderService audienceQuestioStatmentBuilderService;

	/**
	 * @param request
	 * @return Statement
	 */
	public Statement getStatementViaServiceName(HttpServletRequest request) {
		String requestUri = request.getRequestURI().toLowerCase();
		if(requestUri.contains("readcount")) {
			return this.audienceQuestioStatmentBuilderService.buildForUnreadInterposedQuestionsCount(request);
		} else if (requestUri.contains("audiencequestion")) {
			String methodName = request.getMethod().toLowerCase();
			switch (methodName) {
				case "post":
					return this.audienceQuestioStatmentBuilderService.buildForAskedInterposedQuestion(request);
				case "get":
					Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
					if (pathVariables.isEmpty()) {
						return this.audienceQuestioStatmentBuilderService.buildForGetInterposedQuestion(request);
					}

					return this.audienceQuestioStatmentBuilderService.buildForGetOneInterposedQuestion(pathVariables);
				case "delete":
					return this.audienceQuestioStatmentBuilderService.buildForDeleteInterposedQuestion(request);
			}
		}

		// This case should only happen if ARSNOVA registers a new action
		// TODO custom exception
		throw new NullPointerException();
	}

}
