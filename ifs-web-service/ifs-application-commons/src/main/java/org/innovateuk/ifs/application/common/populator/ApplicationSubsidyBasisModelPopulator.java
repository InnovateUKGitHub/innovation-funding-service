package org.innovateuk.ifs.application.common.populator;

import org.innovateuk.ifs.application.common.viewmodel.ApplicationSubsidyBasisViewModel;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class ApplicationSubsidyBasisModelPopulator {

    @Autowired
    private ProcessRoleRestService processRoleRestService;
    @Autowired
    private QuestionStatusRestService questionStatusRestService;

    public ApplicationSubsidyBasisViewModel populate(long applicationId, long questionId) {
        List<Long> organisationIds = processRoleRestService.findProcessRole(applicationId).getSuccess()
                .stream().map(processRoleResource -> processRoleResource.getOrganisationId())
                .distinct()
                .collect(toList());
        return new ApplicationSubsidyBasisViewModel(isSubsidyBasisCompletedByAllOrganisations(applicationId, organisationIds, questionId));
    }

    private boolean isSubsidyBasisCompletedByAllOrganisations(long applicationId, List<Long> organisationIds, long questionId) {
        List<Long> organisationIdsThatHaveCompletedQuestion =
                questionStatusRestService.findQuestionStatusesByQuestionAndApplicationId(questionId, applicationId)
                .getSuccess()
                .stream()
                .filter(questionStatus -> questionStatus.getMarkedAsComplete())
                .map(questionStatusResource -> questionStatusResource.getMarkedAsCompleteByOrganisationId())
                .collect(toList());
        return organisationIdsThatHaveCompletedQuestion.containsAll(organisationIds);
    }
}