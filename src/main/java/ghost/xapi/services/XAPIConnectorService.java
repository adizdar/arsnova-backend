package ghost.xapi.services;

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

@Service
public class XAPIConnectorService {

	@Value(value = "${xapi.connector.base-url: https://tlacx311.edutec.guru/lrs-backend/resources/systems/xapi/statements}")
	private String url;

	@Autowired
	private RestTemplateWithBasicAuthFactory restTemplateWithBasicAuthFactory;

	// TODO maybe manual thread connection, investigate what is better
	/**
	 * @param statement
	 */
	@Async("sendXapiExecutor")
	public void send(Statement statement) {
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
			// Catch the error and loggit only we don't wan't to b reak the user flow.
			XAPILogger.ERROR.error(e);
		}
	}
}
