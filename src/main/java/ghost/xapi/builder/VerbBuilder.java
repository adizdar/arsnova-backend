package ghost.xapi.builder;

import ghost.xapi.entities.Verb;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VerbBuilder {

	@Value(value = "${xapi.verb.base-url: http://adlnet.gov/expapi/verbs/}")
	private String baseUrl;

	/**
	 * @param id
	 * @return Verb
	 */
	public Verb createVerb(String id) {
		return new Verb(this.baseUrl + id);
	}
}
