package com.worth.ifs.finance.handler;

import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.handler.item.OrganisationFinanceHandler;
import com.worth.ifs.finance.repository.ApplicationFinanceRepository;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResourceId;
import com.worth.ifs.finance.resource.category.CostCategory;
import com.worth.ifs.finance.resource.cost.CostType;
import com.worth.ifs.user.domain.OrganisationTypeEnum;
import com.worth.ifs.user.repository.OrganisationRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

@Service
public class ApplicationFinanceHandlerImpl implements ApplicationFinanceHandler {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    OrganisationFinanceHandler organisationFinanceHandler;

    @Autowired
    OrganisationRepository organisationRepository;

    @Override
    public ApplicationFinanceResource getApplicationOrganisationFinances(ApplicationFinanceResourceId applicationFinanceResourceId) {
        log.debug("HEAEARG 3");
        ApplicationFinance applicationFinance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(
                applicationFinanceResourceId.getApplicationId(), applicationFinanceResourceId.getOrganisationId());

        ApplicationFinanceResource applicationFinanceResource = null;

        if(applicationFinance!=null) {
            applicationFinanceResource = new ApplicationFinanceResource(applicationFinance);
            setFinanceDetails(applicationFinanceResource);
        }
        log.debug("HEAEARG 4");

        return applicationFinanceResource;
    }

    @Override
    public List<ApplicationFinanceResource> getApplicationTotals(Long applicationId) {
        List<ApplicationFinance> applicationFinances = applicationFinanceRepository.findByApplicationId(applicationId);
        List<ApplicationFinanceResource> applicationFinanceResources = new ArrayList<>();

        for(ApplicationFinance applicationFinance : applicationFinances) {
            ApplicationFinanceResource applicationFinanceResource = new ApplicationFinanceResource(applicationFinance);
            EnumMap<CostType, CostCategory> costs = organisationFinanceHandler.getOrganisationFinanceTotals(applicationFinanceResource.getId());
            applicationFinanceResource.setFinanceOrganisationDetails(costs);
            applicationFinanceResources.add(applicationFinanceResource);
        }

        return applicationFinanceResources;
    }

    @Override
    public BigDecimal getResearchParticipationPercentage(Long applicationId){
        List<ApplicationFinanceResource> applicationFinanceResources = this.getApplicationTotals(applicationId);

        BigDecimal totalCosts = applicationFinanceResources.stream()
                .map(ApplicationFinanceResource::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal researchCosts = applicationFinanceResources.stream()
                .filter(f ->
                                OrganisationTypeEnum.isResearch(organisationRepository.findOne(f.getOrganisation()).getOrganisationType())
                )
                .map(ApplicationFinanceResource::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal researchParticipation = researchCosts.divide(totalCosts, 6, BigDecimal.ROUND_HALF_UP);
        researchParticipation = researchParticipation.multiply(BigDecimal.valueOf(100));
        return researchParticipation.setScale(2, BigDecimal.ROUND_HALF_UP);
    }


    protected void setFinanceDetails(ApplicationFinanceResource applicationFinanceResource) {
        EnumMap<CostType, CostCategory> costs = organisationFinanceHandler.getOrganisationFinances(applicationFinanceResource.getId());
        applicationFinanceResource.setFinanceOrganisationDetails(costs);
    }
}