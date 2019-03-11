package ghost.xapi.statements;

import ghost.xapi.builder.ActivityBuilder;
import ghost.xapi.builder.VerbBuilder;
import ghost.xapi.services.ActorBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class AbstractStatementBuilderService {

	@Autowired
	protected ActivityBuilder activityBuilder;

	@Autowired
	protected VerbBuilder verbBuilder;

	@Autowired
	protected ActorBuilderService actorBuilderService;

	/**
	 * @param parameter
	 *
	 * @return boolean
	 */
	protected boolean parseParameterToBool(String parameter) {
		return Boolean.parseBoolean(parameter);
	}

	/**
	 * @return String
	 */
	protected String generateUUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * @return String
	 */
	protected String getCurrentTimestamp() {
		return String.valueOf(System.currentTimeMillis());
	}

}
