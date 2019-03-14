package ghost.xapi.filter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;


/**
 * @basedOn https://stackoverflow.com/questions/10210645/http-servlet-request-lose-params-from-post-body-after-read-it-once/17129256#17129256
 * @basedOn https://stackoverflow.com/questions/29208456/httpservletrequestwrapper-example-implementation-for-setreadlistener-isfinish/30748533#30748533
 */
public class MultiReadHttpServletRequest extends HttpServletRequestWrapper {
	private ByteArrayOutputStream cachedBytes;

	// @source https://commons.apache.org/proper/commons-io/apidocs/src-html/org/apache/commons/io/IOUtils.html
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	// @source https://commons.apache.org/proper/commons-io/apidocs/src-html/org/apache/commons/io/IOUtils.html
	private static final int EOF = -1;

	public MultiReadHttpServletRequest(HttpServletRequest request) {
		super(request);
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		if (this.cachedBytes == null) {
			cacheInputStream();
		}

		return new CachedServletInputStream();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}

	private void cacheInputStream() throws IOException {
		// Cache the inputstream in order to read it multiple times.
		// For convenience, we use a imported part of apache.commons IOUtils.
		this.cachedBytes = new ByteArrayOutputStream();
		this.copy(super.getInputStream(), this.cachedBytes);
	}

	/**
	 * @param input
	 * @param output
	 *
	 * @return int
	 * @throws IOException
	 * @source https://commons.apache.org/proper/commons-io/apidocs/src-html/org/apache/commons/io/IOUtils.html
	 */
	private int copy(final InputStream input, final OutputStream output) throws IOException {
		final long count = copyLarge(input, output);
		if (count > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) count;
	}

	/**
	 * @param input
	 * @param output
	 *
	 * @return long
	 * @throws IOException
	 * @source https://commons.apache.org/proper/commons-io/apidocs/src-html/org/apache/commons/io/IOUtils.html
	 */
	private long copyLarge(final InputStream input, final OutputStream output)
			throws IOException {
		return this.copy(input, output, DEFAULT_BUFFER_SIZE);
	}

	/**
	 * @param input
	 * @param output
	 * @param bufferSize
	 *
	 * @return
	 * @throws IOException
	 * @source https://commons.apache.org/proper/commons-io/apidocs/src-html/org/apache/commons/io/IOUtils.html
	 */
	private long copy(final InputStream input, final OutputStream output, final int bufferSize)
			throws IOException {
		return this.copyLarge(input, output, new byte[bufferSize]);
	}

	/**
	 * @param input
	 * @param output
	 * @param buffer
	 *
	 * @return
	 * @throws IOException
	 * @source https://commons.apache.org/proper/commons-io/apidocs/src-html/org/apache/commons/io/IOUtils.html
	 */
	private long copyLarge(final InputStream input, final OutputStream output, final byte[] buffer)
			throws IOException {
		long count = 0;
		int n;
		while (EOF != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * An inputstream which reads the cached request body
	 */
	public class CachedServletInputStream extends ServletInputStream {
		private ByteArrayInputStream input;

		public CachedServletInputStream() {
			// create a new input stream from the cached request body
			this.input = new ByteArrayInputStream(cachedBytes.toByteArray());
		}

		@Override
		public int read() throws IOException {
			return this.input.read();
		}

		@Override
		public boolean isFinished() {
			return this.input.available() == 0;
		}

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setReadListener(ReadListener listener) {
			throw new RuntimeException("Not implemented");
		}
	}
}
