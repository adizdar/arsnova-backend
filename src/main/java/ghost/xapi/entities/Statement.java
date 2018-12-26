package ghost.xapi.entities;

public class Statement {

	private Actor actor;
	private Verb verb;
	private Activity activity;

	public Statement(Actor actor, Verb verb, Activity activity) {
		this.actor = actor;
		this.verb = verb;
		this.activity = activity;
	}

	public Actor getActor() {
		return actor;
	}

	public Verb getVerb() {
		return verb;
	}

	public Activity getActivity() {
		return activity;
	}

}
