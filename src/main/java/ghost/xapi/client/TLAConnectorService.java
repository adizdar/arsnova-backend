package ghost.xapi.client;

import ghost.xapi.entities.Statement;
import ghost.xapi.client.RestTemplateWithBasicAuthFactory;
import ghost.xapi.log.XAPILogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.MissingFormatArgumentException;

@Service
public class TLAConnectorService {

	@Value(value = "${xapi.connector.base-url: https://tlacx311.edutec.guru/lrs-backend/resources/systems/xapi/statements}")
	private String url;

	@Autowired
	private RestTemplateWithBasicAuthFactory restTemplateWithBasicAuthFactory;

	// TODO maybe try manual thread connection if it is to slow
	/**
	 * @param statement
	 */
	@Async("sendXapiExecutor")
	public void send(Statement statement) {
		if (this.url.isEmpty()) {
			throw new MissingFormatArgumentException("xapi.connector.base-url is not defined inside of the Arsnova properties file.");
		}

		RestTemplate restTemplate = this.restTemplateWithBasicAuthFactory.getObject();

		try {
			HttpEntity<Statement> requestToTLA = new HttpEntity<>(statement);
			ResponseEntity<String> responseTLA = restTemplate.exchange(
					this.url,
					HttpMethod.POST,
					requestToTLA,
					String.class
			);

			XAPILogger.LOGGER.info("Status code: " + responseTLA.getStatusCode());
		} catch (Exception e) {
			// Only log so the user flow doesn't break.
			XAPILogger.ERROR.error(e);
		}
	}
}
