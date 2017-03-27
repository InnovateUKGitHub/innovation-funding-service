package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.domain.FundingDecisionStatus;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Test;

import java.util.Optional;

import static java.util.Optional.empty;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;

public class ApplicationSummaryServiceSecurityTest extends BaseServiceSecurityTest<ApplicationSummaryService> {


    @Test
    public void test_getApplicationSummariesByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getApplicationSummariesByCompetitionId(1L, null, 0, 20, empty()),
                PROJECT_FINANCE, COMP_ADMIN);
    }

    @Test
    public void test_getSubmittedApplicationSummariesByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getSubmittedApplicationSummariesByCompetitionId(1L, null, 0, 20, empty(), empty()),
                PROJECT_FINANCE, COMP_ADMIN);
    }

    @Test
    public void test_getNotSubmittedApplicationSummariesByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getNotSubmittedApplicationSummariesByCompetitionId(1L, null, 0, 20),
                PROJECT_FINANCE, COMP_ADMIN);
    }

    @Test
    public void test_getFeedbackRequiredApplicationSummariesByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getFeedbackRequiredApplicationSummariesByCompetitionId(1L, null, 0, 20),
                PROJECT_FINANCE, COMP_ADMIN);
    }

    @Test
    public void test_getWithFundingDecisionApplicationSummariesByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getWithFundingDecisionApplicationSummariesByCompetitionId(1L, null, 0, 20, empty(), empty(), empty()),
                PROJECT_FINANCE, COMP_ADMIN);
    }

    @Override
    protected Class<? extends ApplicationSummaryService> getClassUnderTest() {
        return TestApplicationSummaryService.class;
    }

    public static class TestApplicationSummaryService implements ApplicationSummaryService {

		@Override
		public ServiceResult<ApplicationSummaryPageResource> getApplicationSummariesByCompetitionId(Long competitionId, String sortBy, int pageIndex, int pageSize, Optional<String> filter) {
			return null;
		}

        @Override
        public ServiceResult<ApplicationSummaryPageResource> getSubmittedApplicationSummariesByCompetitionId(Long competitionId, String sortBy, int pageIndex, int pageSize, Optional<String> filter, Optional<FundingDecisionStatus> fundingFilter) {
            return null;
        }

        @Override
        public ServiceResult<ApplicationSummaryPageResource> getNotSubmittedApplicationSummariesByCompetitionId(
                Long competitionId, String sortBy, int pageIndex, int pageSize) {
            return null;
        }

        @Override
        public ServiceResult<ApplicationSummaryPageResource> getFeedbackRequiredApplicationSummariesByCompetitionId(
                Long competitionId, String sortBy, int pageIndex, int pageSize) {
            return null;
        }

        @Override
        public ServiceResult<ApplicationSummaryPageResource> getWithFundingDecisionApplicationSummariesByCompetitionId(long competitionId, String sortBy, int pageIndex, int pageSize, Optional<String> filter, Optional<Boolean> sendFilter, Optional<FundingDecisionStatus> fundingFilter) {
            return null;
        }
    }
}
