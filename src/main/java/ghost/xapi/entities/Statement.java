package ghost.xapi.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.thm.arsnova.entities.User;
import ghost.xapi.entities.activity.Activity;
import ghost.xapi.entities.actor.Actor;
import ghost.xapi.entities.verb.Verb;

public class Statement {

	private Actor actor;
	private Verb verb;
	private Activity activity;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Context context;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Result result;

	/**
	 * @param actor
	 * @param verb
	 * @param activity
	 */
	public Statement(Actor actor, Verb verb, Activity activity) {
		this.actor = actor;
		this.verb = verb;
		this.activity = activity;
	}

	/**
	 * @param actor
	 * @param verb
	 * @param activity
	 * @param result
	 */
	public Statement(Actor actor, Verb verb, Activity activity, Result result) {
		this.actor = actor;
		this.verb = verb;
		this.activity = activity;
		this.result = result;
	}

	/**
	 * @param actor
	 * @param verb
	 * @param activity
	 * @param context
	 * @param result
	 */
	public Statement(Actor actor, Verb verb, Activity activity, Result result, Context context) {
		this.actor = actor;
		this.verb = verb;
		this.activity = activity;
		this.context = context;
		this.result = result;
	}

	/**
	 * @return ghost.xapi.entities.actor.Actor
	 */
	public Actor getActor() {
		return actor;
	}

	/**
	 * @return ghost.xapi.entities.verb.verb
	 */
	public Verb getVerb() {
		return verb;
	}

	/**
	 * @return ghost.xapi.entities.activity.activity
	 */
	public Activity getActivity() {
		return activity;
	}

	/**
	 * @return ghost.xapi.entities.Result
	 */
	public Result getResult() {
		return result;
	}

	/**
	 * @param result
	 */
	public void setResult(Result result) {
		this.result = result;
	}

	/**
	 * @return ghost.xapi.entities.Context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * @param context
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * @param user
	 */
	public void addUserRoleToContext(User user) {
		if (user == null || user.getRole() == null) {
			return;
		}

		if (this.getContext() == null) {
			this.setContext(new Context());
		}

		this.getContext().addRole(user.getRole());
	}
}
