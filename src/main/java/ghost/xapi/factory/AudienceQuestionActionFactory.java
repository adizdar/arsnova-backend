package ghost.xapi.factory;

import ghost.xapi.entities.Statement;
import ghost.xapi.services.AudienceQuestioStatmentBuilderService;
import ghost.xapi.services.LoginStatementBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AudienceQuestionActionFactory {

	@Autowired
	private AudienceQuestioStatmentBuilderService audienceQuestioStatmentBuilderService;

	/**
	 * @param request
	 * @param serviceName
	 * @return Statement
	 */
	public Statement getStatementViaServiceName(HttpServletRequest request, String serviceName) {
		switch (serviceName.toLowerCase()) {
			case "audiencequestion":
				this.audienceQuestioStatmentBuilderService.buildForInterposedQuestion(request);
		}

		return null;
	}

}
