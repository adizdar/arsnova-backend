package ghost.xapi.statements.statistics;

import ghost.xapi.entities.Statement;
import ghost.xapi.statements.StatementBuilder;
import ghost.xapi.statements.StatementBuilderBlock;
import ghost.xapi.statements.UriMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class StatisticsActionFactory {

	@Autowired
	private StatisticsStatementBuilderService statisticsStatementBuilderService;

	private StatementBuilderBlock block = new StatementBuilderBlock() {
		@Override
		public Statement create(String requestUri, HttpServletRequest request) {
			if (UriMatchService.doesUriMatchWithPattern(request, "/statistics")
					|| UriMatchService.doesUriMatchWithPattern(request, "/statistics/")) {
				return statisticsStatementBuilderService.buildForGetStatistics(request);
			} else if (requestUri.contains("activeusercount")) {
				return statisticsStatementBuilderService.buildForGetActiveUserCount(request);
			} else if (requestUri.contains("loggedinusercount")) {
				return statisticsStatementBuilderService.buildForGetLogginUserCount(request);
			} else if (requestUri.contains("sessioncount")) {
				return statisticsStatementBuilderService.buildForGetSessionCount(request);
			}
			
			return null;
		}

		@Override
		public Statement createWithIoOperations(String requestUri, HttpServletRequest request) throws IOException {
			// Only placeholder.
			return null;
		}
	};

	/**
	 * @param request
	 *
	 * @return Statement
	 */
	public Statement getStatementViaServiceName(HttpServletRequest request) {
		return StatementBuilder.createFromRequest(request, this.block);
	}

}
