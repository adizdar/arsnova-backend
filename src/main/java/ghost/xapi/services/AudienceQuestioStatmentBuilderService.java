package ghost.xapi.services;

import de.thm.arsnova.entities.InterposedQuestion;
import ghost.xapi.entities.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class AudienceQuestioStatmentBuilderService {

	@Autowired
	private ActorBuilderService actorBuilderService;

	public Statement buildForInterposedQuestion(HttpServletRequest request) {
//		InterposedQuestion interposedQuestion = request.getParameter()
		return new Statement(null, null, null);
	}

}
