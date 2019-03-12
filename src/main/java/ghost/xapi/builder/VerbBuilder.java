package ghost.xapi.builder;

import ghost.xapi.entities.Verb;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VerbBuilder {

	@Value(value = "${root-url}")
	private String rootUrl;
	@Value(value = "${xapi.verb.base-url: http://adlnet.gov/expapi/verbs/}")
	private String baseUrl;

	/**
	 * @param id
	 * @return Verb
	 */
	public Verb createVerb(String id) {
		this.baseUrl = (this.baseUrl != null && !this.baseUrl.isEmpty()) ? this.baseUrl : this.rootUrl;
		return new Verb(this.baseUrl + id);
	}
}
