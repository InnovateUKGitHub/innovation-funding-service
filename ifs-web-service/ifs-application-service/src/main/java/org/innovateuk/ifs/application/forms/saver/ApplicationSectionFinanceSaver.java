package org.innovateuk.ifs.application.forms.saver;

import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;

/**
 * This Saver will handle save all sections that are related to the application.
 */
@Service
public class ApplicationSectionFinanceSaver extends AbstractApplicationSaver {

    @Autowired
    private FinanceRowRestService financeRowRestService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private QuestionService questionService;

    public void handleMarkAcademicFinancesAsNotRequired(Long organisationType, SectionResource selectedSection, Long applicationId, Long competitionId, Long processRoleId) {
        if (SectionType.PROJECT_COST_FINANCES.equals(selectedSection.getType())
                && OrganisationTypeEnum.RESEARCH.getId().equals(organisationType)) {
            SectionResource organisationSection = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.ORGANISATION_FINANCES).get(0);
            SectionResource fundingSection = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.FUNDING_FINANCES).get(0);
            sectionService.markAsNotRequired(organisationSection.getId(), applicationId, processRoleId);
            sectionService.markAsNotRequired(fundingSection.getId(), applicationId, processRoleId);
        }
    }

    public void handleStateAid(Map<String, String[]> params, ApplicationResource application, ApplicationForm form, SectionResource selectedSection) {
        if (isMarkSectionAsCompleteRequest(params)) {
            application.setStateAidAgreed(form.isStateAidAgreed());
        } else if (isMarkSectionAsIncompleteRequest(params) && selectedSection.getType() == SectionType.FINANCE) {
            application.setStateAidAgreed(Boolean.FALSE);
        }
    }

    public ValidationMessages handleRequestFundingRequests(Map<String, String[]> params, Long applicationId, Long userId, Long competitionId, Long processRoleId) {
        if (isNotRequestingFundingRequest(params)) {
            return setRequestingFunding(NOT_REQUESTING_FUNDING, userId, applicationId, competitionId, processRoleId);
        } else {
            return setRequestingFunding(REQUESTING_FUNDING, userId, applicationId, competitionId, processRoleId);
        }
    }

    private ValidationMessages setRequestingFunding(String requestingFunding, Long userId, Long applicationId, Long competitionId, Long processRoleId) {
        ApplicationFinanceResource finance = financeService.getApplicationFinanceDetails(userId, applicationId);
        QuestionResource financeQuestion = questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, FormInputType.FINANCE).getSuccessObjectOrThrowException();
        if (finance.getGrantClaim() != null) {
        }
        ValidationMessages errors = financeRowRestService.add(finance.getId(), financeQuestion.getId(), finance.getGrantClaim()).getOrElse(new ValidationMessages());

        if (!errors.hasErrors()) {
            SectionResource organisationSection = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.ORGANISATION_FINANCES).get(0);
            SectionResource fundingSection = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.FUNDING_FINANCES).get(0);
            if (REQUESTING_FUNDING.equals(requestingFunding)) {
                sectionService.markAsInComplete(organisationSection.getId(), applicationId, processRoleId);
                sectionService.markAsInComplete(fundingSection.getId(), applicationId, processRoleId);
            } else if (NOT_REQUESTING_FUNDING.equals(requestingFunding)) {
                sectionService.markAsNotRequired(organisationSection.getId(), applicationId, processRoleId);
                sectionService.markAsNotRequired(fundingSection.getId(), applicationId, processRoleId);
            }
        }

        return errors;
    }
}
