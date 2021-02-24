package org.innovateuk.ifs.application.common.populator;

import org.innovateuk.ifs.application.common.viewmodel.ApplicationSubsidyBasisPartnerRowViewModel;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationSubsidyBasisViewModel;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.questionnaire.link.service.QuestionnaireResponseLinkRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.SortedSet;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.LEADAPPLICANT;

@Component
public class ApplicationSubsidyBasisModelPopulator {

    @Autowired
    private ProcessRoleRestService processRoleRestService;
    @Autowired
    private QuestionStatusRestService questionStatusRestService;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private FinanceService financeService;
    @Autowired
    private QuestionnaireResponseLinkRestService questionnaireResponseLinkRestService;

    public ApplicationSubsidyBasisViewModel populate(QuestionResource question, long applicationId) {
        List<Long> organisationsThatHaveCompletedQuestion = organisationsThatHaveCompletedQuestion(question.getId(), applicationId);
        long leadOrganisation = leadOrganisationId(applicationId);
        List<ApplicationSubsidyBasisPartnerRowViewModel> partnerRows = organisationsForApplication(applicationId)
                .stream()
                .map(organisation ->
                   new ApplicationSubsidyBasisPartnerRowViewModel(
                           organisation.getName(),
                           leadOrganisation == organisation.getId(),
                           northernIslandDeclaration(organisation.getId(), applicationId),
                           questionnaireResponseId(question.getQuestionnaireId(), organisation.getId(), applicationId),
                           organisationHasCompletedQuestion(organisation.getId(), organisationsThatHaveCompletedQuestion))
                ).collect(toList());
        return new ApplicationSubsidyBasisViewModel(partnerRows);
    }

    private long leadOrganisationId(long applicationId){
        return processRoleRestService.findProcessRole(applicationId).getSuccess()
                .stream()
                .filter(processRole -> LEADAPPLICANT.equals(processRole.getRole()))
                .findFirst()
                .map(ProcessRoleResource::getOrganisationId)
                .get();
    }

    private SortedSet<OrganisationResource> organisationsForApplication(long applicationId){
        List<ProcessRoleResource> userApplicationRoles = processRoleRestService.findProcessRole(applicationId).getSuccess();
        return organisationService.getApplicationOrganisations(userApplicationRoles);
    }

    private List<Long> organisationsThatHaveCompletedQuestion(long questionId, long applicationId){
        return questionStatusRestService.findQuestionStatusesByQuestionAndApplicationId(questionId, applicationId)
                .getSuccess()
                .stream()
                .filter(questionStatus -> questionStatus.getMarkedAsComplete())
                .map(questionStatusResource -> questionStatusResource.getMarkedAsCompleteByOrganisationId())
                .collect(toList());
    }

    private String questionnaireResponseId(long questionnaireId, long organisationId, long applicationId){
        return questionnaireResponseLinkRestService.getResponseIdByApplicationIdAndOrganisationIdAndQuestionnaireId(questionnaireId, applicationId, organisationId)
                .getSuccess()
                .getContent();

    }

    private Boolean northernIslandDeclaration(long organisationId, long applicationId){
        return financeService.getApplicationFinanceByApplicationIdAndOrganisationId(applicationId, organisationId).getNorthernIrelandDeclaration();
    }

    private boolean organisationHasCompletedQuestion(long organisationId, List<Long> organisationsThatHaveCompletedQuestion){
        return organisationsThatHaveCompletedQuestion.contains(organisationId);
    }
}