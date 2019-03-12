package ghost.xapi.config;

import ghost.xapi.statements.authentication.AuthenticationActionFactory;
import ghost.xapi.factory.StatementBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

	/**
	 * @return StatementBuilderFactory
	 */
	@Bean
	public StatementBuilderFactory statementBuilderFactory() {
		return new StatementBuilderFactory();
	}

	/**
	 * @return LoginActionFactory
	 */
	@Bean
	public AuthenticationActionFactory loginActionFactory() {
		return new AuthenticationActionFactory();
	}

}
