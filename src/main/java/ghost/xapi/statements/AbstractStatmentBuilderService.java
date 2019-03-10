package ghost.xapi.statements;

import ghost.xapi.builder.ActivityBuilder;
import ghost.xapi.builder.VerbBuilder;
import ghost.xapi.services.ActorBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AbstractStatmentBuilderService {

	@Autowired
	protected ActivityBuilder activityBuilder;

	@Autowired
	protected VerbBuilder verbBuilder;

	@Autowired
	protected ActorBuilderService actorBuilderService;

}
