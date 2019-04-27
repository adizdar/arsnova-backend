package ghost.xapi.statements.statistics;

import de.thm.arsnova.entities.Statistics;
import de.thm.arsnova.services.IStatisticsService;
import ghost.xapi.entities.Result;
import ghost.xapi.entities.Statement;
import ghost.xapi.entities.actor.Actor;
import ghost.xapi.statements.AbstractStatementBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Mapping path: "/statistics"
 */
@Service
public class StatisticsStatementBuilderService extends AbstractStatementBuilderService {

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
				"statistics"
		});

		Statistics statistics = this.statisticsService.getStatistics();

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				this.activityBuilder.createActivity(activityId, "statistics"),
				new Result("statistics", new Object[] { statistics })
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
				new Result("activeUserCount", new Object[] { Integer.toString(this.statisticsService.getStatistics().getActiveUsers()) })
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
				"loggedinusercount/statistics"
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("count"),
				this.activityBuilder.createActivity(activityId, "loginUsers"),
				new Result("loggedInUserCount", new Object[] { Integer.toString(this.statisticsService.getStatistics().getLoggedinUsers()) })
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
				"sessioncount/statistics"
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("count"),
				this.activityBuilder.createActivity(activityId, "sessions"),
				new Result("sessionCount", new Object[] { Integer.toString(this.statisticsService.getStatistics().getOpenSessions()
						+ this.statisticsService.getStatistics().getClosedSessions()) })
		);
	}
}
