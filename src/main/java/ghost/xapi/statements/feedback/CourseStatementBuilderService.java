package ghost.xapi.statements.feedback;

import de.thm.arsnova.entities.Statistics;
import de.thm.arsnova.services.IStatisticsService;
import ghost.xapi.entities.Result;
import ghost.xapi.entities.Statement;
import ghost.xapi.statements.AbstractStatementBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Mapping path: "/statistics"
 */
@Service
public class FeedbackStatementBuilderService extends AbstractStatementBuilderService {

	@Autowired
	private IStatisticsService statisticsService;

	/**
	 * @param request method GET
	 * path /
	 *
	 * @return
	 */
	public Statement buildForGetStatistics(HttpServletRequest request) {
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"statistics",
				this.generateUUID()
		});

		Statistics statistics = this.statisticsService.getStatistics();

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				this.activityBuilder.createActivity(activityId, "statistics"),
				new Result(new Object[] { statistics })
		);
	}

	/**
	 * @param request method GET
	 * path /activeusercount
	 *
	 * @return
	 */
	public Statement buildForGetActiveUserCount(HttpServletRequest request) {
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"activeusercount/statistics",
				this.generateUUID()
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("count"),
				this.activityBuilder.createActivity(activityId, "activeUsers"),
				new Result(new Object[] { Integer.toString(this.statisticsService.getStatistics().getActiveUsers()) })
		);
	}

	/**
	 * @param request method GET
	 * path /loggedinusercount
	 *
	 * @return
	 */
	public Statement buildForGetLogginUserCount(HttpServletRequest request) {
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"loggedinusercount/statistics",
				this.generateUUID()
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("count"),
				this.activityBuilder.createActivity(activityId, "loginUsers"),
				new Result(new Object[] { Integer.toString(this.statisticsService.getStatistics().getLoggedinUsers()) })
		);
	}

	/**
	 * @param request method GET
	 * path /sessioncount
	 *
	 * @return
	 */
	public Statement buildForGetSessionCount(HttpServletRequest request) {
		String activityId = this.activityBuilder.createActivityId(new String[]{
				"sessioncount/statistics",
				this.generateUUID()
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("count"),
				this.activityBuilder.createActivity(activityId, "sessions"),
				new Result(new Object[] { Integer.toString(this.statisticsService.getStatistics().getOpenSessions()
						+ this.statisticsService.getStatistics().getClosedSessions()) })
		);
	}
}
