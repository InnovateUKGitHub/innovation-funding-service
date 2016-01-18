package com.worth.ifs.finance.handler;

import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.handler.item.OrganisationFinanceHandler;
import com.worth.ifs.finance.repository.ApplicationFinanceRepository;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.category.CostCategory;
import com.worth.ifs.finance.resource.cost.CostType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

@Service
public class ApplicationFinanceHandlerImpl implements ApplicationFinanceHandler {

    @Autowired
    ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    OrganisationFinanceHandler organisationFinanceHandler;

    @Override
    public ApplicationFinanceResource getApplicationOrganisationFinances(Long applicationId, Long organisationId) {
        ApplicationFinance applicationFinance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(applicationId, organisationId);
        ApplicationFinanceResource applicationFinanceResource = null;

        if(applicationFinance!=null) {
            applicationFinanceResource = new ApplicationFinanceResource(applicationFinance);
            setFinanceDetails(applicationFinanceResource);
        }

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

    protected void setFinanceDetails(ApplicationFinanceResource applicationFinanceResource) {
        EnumMap<CostType, CostCategory> costs = organisationFinanceHandler.getOrganisationFinances(applicationFinanceResource.getId());
        applicationFinanceResource.setFinanceOrganisationDetails(costs);
    }
}
