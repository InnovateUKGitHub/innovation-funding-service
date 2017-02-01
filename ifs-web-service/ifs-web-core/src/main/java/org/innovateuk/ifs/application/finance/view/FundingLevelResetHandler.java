package org.innovateuk.ifs.application.finance.view;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.finance.service.FinanceRowService;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handler to reset finance funding levels and mark associated section as incomplete
 */
@Component
public class FundingLevelResetHandler {

    private static final Log LOG = LogFactory.getLog(FundingLevelResetHandler.class);

    @Autowired
    private FinanceService financeService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private FinanceRowService financeRowService;

    public void resetFundingAndMarkAsIncomplete(ApplicationFinanceResource applicationFinance, Long competitionId, Long userId) {

        Optional<ProcessRoleResource> processRole = processRoleService.getByApplicationId(applicationFinance.getApplication()).stream()
                .filter(processRoleResource -> userId.equals(processRoleResource.getUser()))
                .findFirst();

        //set your funding section to marked as incomplete
        sectionService.getSectionsForCompetitionByType(competitionId, SectionType.FUNDING_FINANCES)
                .forEach(fundingSection ->
                        sectionService.markAsInComplete(fundingSection.getId(),
                                applicationFinance.getApplication(),
                                (processRole.isPresent() ? processRole.get().getId() : null))
                );

        QuestionResource financeQuestion = questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, FormInputType.FINANCE).getSuccessObjectOrThrowException();

        resetFundingLevel(applicationFinance, financeQuestion.getId());
    }

    public void resetFundingLevelAndMarkAsIncompleteForAllCollaborators(Long competitionId, Long applicationId) {

        QuestionResource financeQuestion = questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, FormInputType.FINANCE).getSuccessObjectOrThrowException();

        Set<Long> userIds = processRoleService.getByApplicationId(applicationId).stream().filter(processRole -> processRole.getUser() != null).map(
                processRole -> processRole.getUser()).collect(Collectors.toSet());

        userIds.stream().forEach(userId ->
                //set user's funding section to marked as incomplete
                sectionService.getSectionsForCompetitionByType(competitionId, SectionType.FUNDING_FINANCES)
                        .forEach(fundingSection ->
                                sectionService.markAsInComplete(fundingSection.getId(),
                                        applicationId, userId))
        );

        financeService.getApplicationFinanceDetails(applicationId).stream().forEach(applicationFinance -> {
            resetFundingLevel(applicationFinance, financeQuestion.getId());
            applicationFinanceRestService.update(applicationFinance.getId(), applicationFinance);
        });
    }

    public void resetFundingLevel(ApplicationFinanceResource applicationFinance, Long financeQuestionId) {
        if (applicationFinance.getGrantClaim() != null) {
            applicationFinance.getGrantClaim().setGrantClaimPercentage(0);
            financeRowService.add(applicationFinance.getId(), financeQuestionId, applicationFinance.getGrantClaim());
        }
    }
}
