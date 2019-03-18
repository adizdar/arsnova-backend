package ghost.xapi.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import ghost.xapi.entities.activity.Activity;
import ghost.xapi.entities.actor.Actor;

public class Statement {

	private Actor actor;
	private Verb verb;
	private Activity activity;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Result result;

	// TODO make better
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private FailedStatementCreationException failedStatementCreationException;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String errorMessage;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private StackTraceElement[] stackTrace;

	/**
	 * @param failedStatementCreationException
	 */
	public Statement(FailedStatementCreationException failedStatementCreationException) {
		this.failedStatementCreationException = failedStatementCreationException;
	}

	/**
	 * @param stackTrace
	 * @param errorMessage
	 */
	public Statement(StackTraceElement[] stackTrace, String errorMessage) {
		this.stackTrace = stackTrace;
		this.errorMessage = errorMessage;
	}

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
	 * @return ghost.xapi.entities.FailedStatementCreationException
	 */
	public FailedStatementCreationException getFailedStatementCreationException() {
		return failedStatementCreationException;
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
	 * @return java.lang.String
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @return java.lang.StackTraceElement[]
	 */
	public StackTraceElement[] getStackTrace() {
		return stackTrace;
	}
}
