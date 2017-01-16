package org.innovateuk.ifs.application.transactional.sectionupdater;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.resource.QuestionType;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.transactional.QuestionService;
import org.innovateuk.ifs.application.transactional.SectionService;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.transactional.FinanceRowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Handle specific actions when this part gets save in the applicationForm
 */
@Service
public class ApplicationFinanceOrganisationUpdater implements ApplicationFinanceUpdater {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FinanceRowService financeRowService;

    @Override
    public SectionType getRelatedSection() {
        return SectionType.ORGANISATION_FINANCES;
    }

    @Override
    public void handleMarkAsInComplete(Application currentApplication, Section currentSection, Long processRoleId) {

    }

    @Override
    public void handleMarkAsComplete(Application currentApplication, Section currentSection, Long processRoleId) {

        Optional<ApplicationFinance> applicationFinance = getApplicationFinance(currentApplication.getApplicationFinances(), processRoleId);

        sectionService.getSectionsByCompetitionIdAndType(currentSection.getCompetition().getId(), SectionType.FUNDING_FINANCES)
        .andOnSuccess(sectionList -> {
            sectionList.stream().forEach(fundingSection -> {
                resetFundingSectionToMarkedAsIncomplete(fundingSection.getId(), currentApplication.getId(), processRoleId);
                resetFundingSectionQuestionInputs(fundingSection.getId(), applicationFinance);
            });
            return serviceSuccess();
        });
    }

    private Optional<ApplicationFinance> getApplicationFinance(List<ApplicationFinance> applicationFinances, Long processRoleId) {
        return applicationFinances.stream()
                .filter(applicationFinance -> applicationFinance.getOrganisation().getProcessRoles().stream()
                        .anyMatch(processRole -> processRoleId.equals(processRole.getId())))
                .findAny();
    }

    private void resetFundingSectionQuestionInputs(Long sectionId, Optional<ApplicationFinance> applicationFinance) {
        applicationFinance.ifPresent(applicationFinanceFound -> {
            questionService.getQuestionsBySectionIdAndType(sectionId, QuestionType.GENERAL).andOnSuccess(questionList -> {
                questionList
                    .forEach(questionToBeReset -> {
                        resetFinanceRowInput(applicationFinanceFound.getId(), questionToBeReset.getId());
                    });
                return serviceSuccess();
            });
        });
    }

    private void resetFinanceRowInput(Long applicationFinanceId, Long questionId) {
        financeRowService.getCosts(applicationFinanceId, "grant-claim", questionId).andOnSuccess(financeRowToBeReset -> {
            financeRowToBeReset.forEach(financeRow -> {
                financeRowService.deleteCost(financeRow.getId());
            });
            return serviceSuccess();
        });
    }

    private void resetFundingSectionToMarkedAsIncomplete(Long sectionId, Long applicationId, Long processRoleId) {
        sectionService.markSectionAsInComplete(sectionId, applicationId, processRoleId);
    }
}
