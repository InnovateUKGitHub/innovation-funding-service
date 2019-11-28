package org.innovateuk.ifs.finance.handler;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.mapper.ProjectFinanceMapper;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResourceId;
import org.innovateuk.ifs.finance.resource.category.ChangedFinanceRowPair;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Handler for retrieving project finance data.
 */
@Component
public class ProjectFinanceHandlerImpl implements ProjectFinanceHandler {
    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private ProjectFinanceMapper projectFinanceMapper;

    @Autowired
    private OrganisationFinanceDelegate organisationFinanceDelegate;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Override
    public BigDecimal getResearchParticipationPercentageFromProject(long projectId){
        List<ProjectFinanceResource> applicationFinanceResources = this.getFinanceChecksTotals(projectId);

        BigDecimal totalCosts = applicationFinanceResources.stream()
                .map(ProjectFinanceResource::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal researchCosts = applicationFinanceResources.stream()
                .filter(f ->
                        OrganisationTypeEnum.isResearchParticipationOrganisation(organisationRepository.findById(f.getOrganisation()).get().getOrganisationType().getId())
                )
                .map(ProjectFinanceResource::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal researchParticipation = BigDecimal.ZERO;

        if(totalCosts.compareTo(BigDecimal.ZERO)!=0) {
            researchParticipation = researchCosts.divide(totalCosts, 6, BigDecimal.ROUND_HALF_UP);
        }
        researchParticipation = researchParticipation.multiply(BigDecimal.valueOf(100));
        return researchParticipation.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public ServiceResult<ProjectFinanceResource> getProjectOrganisationFinances(ProjectFinanceResourceId projectFinanceResourceId) {
        return find(projectFinanceRepository.findByProjectIdAndOrganisationId(projectFinanceResourceId.getProjectId(), projectFinanceResourceId.getOrganisationId()), notFoundError(ProjectFinance.class)).
                andOnSuccessReturn(
                        projectFinance -> {
                            ProjectFinanceResource projectFinanceResource = null;
                            if(projectFinance!=null) {
                                projectFinanceResource = projectFinanceMapper.mapToResource(projectFinance);
                                setProjectFinanceDetails(projectFinanceResource, projectFinance.getProject().getApplication().getCompetition());
                            }
                            return projectFinanceResource;
                        }
                );
    }

    @Override
    public List<ProjectFinanceResource> getFinanceChecksTotals(long projectId) {
        List<ProjectFinance> finances = projectFinanceRepository.findByProjectId(projectId);
        List<ProjectFinanceResource> financeResources = new ArrayList<>();

        finances.forEach(finance -> {
            ProjectFinanceResource financeResource = projectFinanceMapper.mapToResource(finance);
            OrganisationTypeFinanceHandler organisationFinanceHandler =
                    organisationFinanceDelegate.getOrganisationFinanceHandler(finance.getProject().getApplication().getCompetition().getId(), finance.getOrganisation().getOrganisationType().getId());
            EnumMap<FinanceRowType, FinanceRowCostCategory> costs =
                    new EnumMap<>(organisationFinanceHandler.getProjectOrganisationFinances(financeResource.getId()));
            financeResource.setFinanceOrganisationDetails(costs);
            financeResources.add(financeResource);
        });
        return financeResources;
    }

    private void setProjectFinanceDetails(ProjectFinanceResource projectFinanceResource, Competition competition) {
        Organisation organisation = organisationRepository.findById(projectFinanceResource.getOrganisation()).get();
        OrganisationTypeFinanceHandler organisationFinanceHandler = organisationFinanceDelegate.getOrganisationFinanceHandler(competition.getId(), organisation.getOrganisationType().getId());
        Map<FinanceRowType, FinanceRowCostCategory> costs = organisationFinanceHandler.getProjectOrganisationFinances(projectFinanceResource.getId());
        projectFinanceResource.setFinanceOrganisationDetails(costs);

        Map<FinanceRowType, List<ChangedFinanceRowPair>> costChanges =
                organisationFinanceHandler.getProjectOrganisationFinanceChanges(projectFinanceResource.getId());
        projectFinanceResource.setCostChanges(costChanges);
    }
}
