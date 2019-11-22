package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;

public class AbstractOrganisationFinanceServiceTest extends BaseServiceUnitTest<AbstractOrganisationFinanceService> {


    @Override
    protected AbstractOrganisationFinanceService supplyServiceUnderTest() {
        return new AbstractOrganisationFinanceService() {
            @Override
            protected ServiceResult getFinance(long targetId, long organisationId) {
                return null;
            }

            @Override
            protected ServiceResult<Void> updateFinance(BaseFinanceResource finance) {
                return null;
            }

            @Override
            protected ServiceResult<FinanceRowItem> saveGrantClaim(GrantClaim grantClaim) {
                return null;
            }

            @Override
            protected ServiceResult<CompetitionResource> getCompetitionFromTargetId(long targetId) {
                return null;
            }

            @Override
            protected void resetYourFundingSection(BaseFinanceResource finance, long competitionId, long userId) {

            }

            @Override
            protected ServiceResult<Void> updateStateAidAgreed(long targetId, boolean stateAidAgreed) {
                return null;
            }
        };
    }
}
