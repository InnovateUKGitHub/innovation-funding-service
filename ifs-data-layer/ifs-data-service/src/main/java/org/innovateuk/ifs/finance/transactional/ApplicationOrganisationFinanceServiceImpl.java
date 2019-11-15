package org.innovateuk.ifs.finance.transactional;

import java.time.format.DateTimeFormatter;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.application.transactional.SectionStatusService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.transactional.UsersRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationOrganisationFinanceServiceImpl extends AbstractOrganisationFinanceService<ApplicationFinanceResource> implements ApplicationOrganisationFinanceService {

    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private ApplicationFinanceService financeService;
    @Autowired
    private ApplicationFinanceRowService financeRowCostsService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private UsersRolesService usersRolesService;
    @Autowired
    private SectionStatusService sectionStatusService;

    static final String ANNUAL_TURNOVER_FORM_INPUT_DESCRIPTION = "Annual turnover";
    static final String ANNUAL_PROFITS_FORM_INPUT_DESCRIPTION = "Annual profits";
    static final String ANNUAL_EXPORT_FORM_INPUT_DESCRIPTION = "Annual export";
    static final String RESEARCH_AND_DEVELOPMENT_FORM_INPUT_DESCRIPTION = "Research and development spend";

    @Override
    protected ServiceResult<ApplicationFinanceResource> getFinance(long applicationId, long organisationId) {
        return financeService.financeDetails(applicationId, organisationId);
    }

    @Override
    protected ServiceResult<Void> updateFinance(ApplicationFinanceResource finance) {
        return financeService.updateApplicationFinance(finance.getId(), finance).andOnSuccessReturnVoid();
    }

    @Override
    protected ServiceResult<FinanceRowItem> saveGrantClaim(GrantClaim grantClaim) {
        return financeRowCostsService.update(grantClaim.getId(), grantClaim);
    }

    @Override
    protected ServiceResult<CompetitionResource> getCompetitionFromTargetId(long applicationId) {
        return applicationService.getCompetitionByApplicationId(applicationId);
    }

    @Override
    protected void resetYourFundingSection(ApplicationFinanceResource applicationFinance, long competitionId, long userId) {
        final ProcessRoleResource processRole =
                usersRolesService.getAssignableProcessRolesByApplicationId(applicationFinance.getApplication()).getSuccess().stream()
                        .filter(processRoleResource -> processRoleResource.getUser().equals(userId))
                        .findFirst().get();

        sectionService.getSectionsByCompetitionIdAndType(competitionId, SectionType.FUNDING_FINANCES).getSuccess()
                .forEach(fundingSection ->
                        sectionStatusService.markSectionAsInComplete(
                                fundingSection.getId(),
                                applicationFinance.getApplication(),
                                processRole.getId()
                        ));

    }

    @Override
    protected ServiceResult<Boolean> getStateAidAgreed(long targetId) {
        return applicationService.getApplicationById(targetId).
                andOnSuccessReturn(ApplicationResource::getStateAidAgreed);
    }

    @Override
    protected ServiceResult<Void> updateStateAidAgreed(long targetId, boolean stateAidAgreed) {
        return applicationService.getApplicationById(targetId).
                andOnSuccess(application -> {
                    application.setStateAidAgreed(stateAidAgreed);
                    return applicationService.saveApplicationDetails(targetId, application);
                }).
                andOnSuccessReturnVoid();
    }
}