package ghost.xapi.statements.feedback;

import de.thm.arsnova.connector.client.ConnectorClient;
import de.thm.arsnova.connector.model.Course;
import de.thm.arsnova.connector.model.UserRole;
import de.thm.arsnova.entities.User;
import de.thm.arsnova.services.IUserService;
import ghost.xapi.entities.Result;
import ghost.xapi.entities.Statement;
import ghost.xapi.statements.AbstractStatementBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapping path: "/mycourses"
 */
@Service
public class CourseStatementBuilderService extends AbstractStatementBuilderService {

	@Autowired
	private IUserService userService;

	@Autowired(required = false)
	private ConnectorClient connectorClient;

	/**
	 * @param request method GET
	 * path /mycourses
	 *
	 * @return
	 */
	public Statement buildForGetCourses(HttpServletRequest request) {
		final User currentUser = this.userService.getCurrentUser();

		final List<Course> result = new ArrayList<>();
		for (final Course course : this.connectorClient.getCourses(currentUser.getUsername()).getCourse()) {
			if (
					course.getMembership().isMember()
							&& course.getMembership().getUserrole().equals(UserRole.TEACHER)
			) {
				result.add(course);
			}
		}

		String activityId = this.activityBuilder.createActivityId(new String[]{
				"myCourses",
				this.generateUUID()
		});

		return new Statement(
				this.actorBuilderService.getActor(),
				this.verbBuilder.createVerb("retrieve"),
				this.activityBuilder.createActivity(activityId, "userCourses"),
				new Result(new Object[] { result })
		);
	}
}
