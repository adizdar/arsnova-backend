package ghost.xapi.client;

import org.apache.http.HttpHost;
import org.apache.http.client.AuthCache;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.protocol.BasicHttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.net.URI;

public class HttpComponentsClientHttpRequestFactoryBasicAuth extends HttpComponentsClientHttpRequestFactory {

	private HttpHost host;

	/**
	 * @param host
	 */
	public HttpComponentsClientHttpRequestFactoryBasicAuth(HttpHost host) {
		super();
		this.host = host;
	}

	/**
	 * @param httpMethod
	 * @param uri
	 * @return BasicHttpContext
	 */
	@Override
	protected BasicHttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
		return createHttpContext();
	}

	/**
	 * @return BasicHttpContext
	 */
	private BasicHttpContext createHttpContext() {
		AuthCache authCache = new BasicAuthCache();

		BasicScheme basicAuth = new BasicScheme();
		authCache.put(host, basicAuth);

		BasicHttpContext localContext = new BasicHttpContext();
		localContext.setAttribute(HttpClientContext.AUTH_CACHE, authCache);

		return localContext;
	}
}

