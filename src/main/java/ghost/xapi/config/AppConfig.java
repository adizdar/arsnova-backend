package ghost.xapi.config;

import ghost.xapi.entities.Verb;
import ghost.xapi.factory.LoginActionFactory;
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
	public LoginActionFactory loginActionFactory() {
		return new LoginActionFactory();
	}

}
