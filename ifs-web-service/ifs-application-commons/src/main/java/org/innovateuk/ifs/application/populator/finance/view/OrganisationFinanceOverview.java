package org.innovateuk.ifs.application.populator.finance.view;

import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.beans.factory.annotation.Configurable;

import java.math.BigDecimal;
import java.util.Map;

@Configurable
public interface OrganisationFinanceOverview {

    Map<Long, BaseFinanceResource> getFinancesByOrganisation();

    Map<FinanceRowType, BigDecimal> getTotalPerType();

    BigDecimal getTotal();

    BigDecimal getTotalFundingSought();

    BigDecimal getTotalContribution();

    BigDecimal getTotalOtherFunding();

}
