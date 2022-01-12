package org.innovateuk.ifs.application.finance.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.beans.factory.annotation.Configurable;

import java.math.BigDecimal;
import java.util.Map;

@Configurable
public interface OrganisationFinanceOverview {

    Map<Long, BaseFinanceResource> getFinancesByOrganisation();

    Map<FinanceRowType, BigDecimal> getTotalPerType(CompetitionResource competition);

    BigDecimal getTotal();

    BigDecimal getTotalFundingSought();

    BigDecimal getTotalContribution();

    BigDecimal getTotalOtherFunding();

}
