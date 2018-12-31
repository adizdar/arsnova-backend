package ghost.xapi.builder;

import ghost.xapi.entities.Actor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpSession;

public class StatementBuilder {

	public void build() {
	}

	private Actor getActorFromSession() {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = requestAttributes.getRequest().getSession();

		// TODO error handling if not actor, error should only be logged
		// TODO thread location ??
		return (Actor) session.getAttribute(Actor.class.toString());
	}

}
