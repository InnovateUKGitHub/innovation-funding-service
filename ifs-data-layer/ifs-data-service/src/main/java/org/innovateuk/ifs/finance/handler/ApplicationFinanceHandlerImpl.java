package org.innovateuk.ifs.finance.handler;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.mapper.ApplicationFinanceMapper;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResourceId;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ApplicationFinanceHandlerImpl handles the finance information on application
 * level.
 */
@Service
public class ApplicationFinanceHandlerImpl implements ApplicationFinanceHandler {

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private OrganisationFinanceDelegate organisationFinanceDelegate;

    @Autowired
    private ApplicationFinanceMapper applicationFinanceMapper;

    @Override
    public ApplicationFinanceResource getApplicationOrganisationFinances(ApplicationFinanceResourceId applicationFinanceResourceId) {
        ApplicationFinance applicationFinance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(
                applicationFinanceResourceId.getApplicationId(), applicationFinanceResourceId.getOrganisationId());
        ApplicationFinanceResource applicationFinanceResource = null;

        if (applicationFinance != null) {
            applicationFinanceResource = applicationFinanceMapper.mapToResource(applicationFinance);
            setApplicationFinanceDetails(applicationFinanceResource, applicationFinance.getApplication().getCompetition());
        }

        return applicationFinanceResource;
    }

    @Override
    public List<ApplicationFinanceResource> getApplicationFinances(Long applicationId) {
        return getApplicationFinanceResources(applicationId);
    }

    @Override
    public List<ApplicationFinanceResource> getApplicationTotals(Long applicationId) {
        return getApplicationFinanceResources(applicationId);
    }

    private List<ApplicationFinanceResource> getApplicationFinanceResources(Long applicationId) {
        List<ApplicationFinance> applicationFinances = applicationFinanceRepository.findByApplicationId(applicationId);
        List<ApplicationFinanceResource> applicationFinanceResources = new ArrayList<>();

        for (ApplicationFinance applicationFinance : applicationFinances) {
            OrganisationTypeFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(applicationFinance.getApplication().getCompetition().getId(), applicationFinance.getOrganisation().getOrganisationType().getId());
            Map<FinanceRowType, FinanceRowCostCategory> costs = organisationFinanceHandler.getOrganisationFinances(applicationFinance.getId());

            ApplicationFinanceResource applicationFinanceResource = applicationFinanceMapper.mapToResource(applicationFinance);
            applicationFinanceResource.setFinanceOrganisationDetails(costs);

            applicationFinanceResources.add(applicationFinanceResource);
        }
        return applicationFinanceResources;
    }

    @Override
    public BigDecimal getResearchParticipationPercentage(Long applicationId) {
        List<ApplicationFinanceResource> applicationFinanceResources = this.getApplicationTotals(applicationId);

        BigDecimal totalCosts = applicationFinanceResources.stream()
                .map(ApplicationFinanceResource::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal researchCosts = applicationFinanceResources.stream()
                .filter(f ->
                        OrganisationTypeEnum.isResearchParticipationOrganisation(organisationRepository.findById(f.getOrganisation()).get().getOrganisationType().getId())
                )
                .map(ApplicationFinanceResource::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal researchParticipation = BigDecimal.ZERO;

        if (totalCosts.compareTo(BigDecimal.ZERO) != 0) {
            researchParticipation = researchCosts.divide(totalCosts, 6, BigDecimal.ROUND_HALF_UP);
        }
        researchParticipation = researchParticipation.multiply(BigDecimal.valueOf(100));
        return researchParticipation.setScale(2, BigDecimal.ROUND_CEILING);
    }

    private void setApplicationFinanceDetails(ApplicationFinanceResource applicationFinanceResource, Competition competition) {
        Organisation organisation = organisationRepository.findById(applicationFinanceResource.getOrganisation()).get();
        OrganisationTypeFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(competition.getId(), organisation.getOrganisationType().getId());
        Map<FinanceRowType, FinanceRowCostCategory> costs = organisationFinanceHandler.getOrganisationFinances(applicationFinanceResource.getId());
        applicationFinanceResource.setFinanceOrganisationDetails(costs);
    }
}
