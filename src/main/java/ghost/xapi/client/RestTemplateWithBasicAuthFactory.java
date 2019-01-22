package ghost.xapi.client;

import org.apache.http.HttpHost;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class RestTemplateWithBasicAuthFactory implements FactoryBean<RestTemplate>, InitializingBean {

	@Value(value = "${xapi.connector.basic-auth.username}")
	private String username;
	@Value(value = "${xapi.connector.basic-auth.password}")
	private String password;

	@Value(value = "${xapi.connector.host.name: localhost}")
	private String hostName;
	@Value(value = "${xapi.connector.host.port: 8080}")
	private String port;
	@Value(value = "${xapi.connector.host.scheme: http}")
	private String scheme;

	private RestTemplate restTemplate;

	/**
	 * @return RestTemplate
	 */
	@Override
	public RestTemplate getObject() {
		// If the Basic auth is not set/cleared, set it again.
		if (!this.isBasicAuthSet()) {
			this.addBasicAuthInterceptor();
		}

		return this.restTemplate;
	}

	/**
	 * @return Class<RestTemplate>
	 */
	@Override
	public Class<RestTemplate> getObjectType() {
		return RestTemplate.class;
	}

	/**
	 * @return boolean
	 */
	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() {
		HttpHost host = new HttpHost(this.hostName, Integer.parseInt(this.port), this.scheme);

		this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactoryBasicAuth(host));
		this.addBasicAuthInterceptor();
	}

	/**
	 * @return boolean
	 */
	private boolean isBasicAuthSet() {
		return this.restTemplate.getInterceptors().stream().filter(
				interceptor -> interceptor instanceof BasicAuthorizationInterceptor
		).findFirst().isPresent();
	}

	private void addBasicAuthInterceptor() {
		this.restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(this.username,this.password));
	}

}
