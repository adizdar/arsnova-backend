package ghost.xapi.entities;

import ghost.xapi.entities.activity.Activity;
import ghost.xapi.entities.actor.Actor;

public class Statement {

	private Actor actor;
	private Verb verb;
	private Activity activity;

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

}
