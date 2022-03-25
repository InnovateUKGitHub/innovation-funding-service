package org.innovateuk.ifs.application.forms.questions.horizon.populator;

import org.innovateuk.ifs.application.forms.questions.horizon.model.HorizonWorkProgrammeViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.LEADAPPLICANT;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.applicantProcessRoles;

@Component
public class HorizonWorkProgrammePopulator {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private QuestionStatusRestService questionStatusRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRestService userRestService;

    public HorizonWorkProgrammeViewModel populate(long applicationId,
                                                  long questionId,
                                                  UserResource user,
                                                  String pageTitle,
                                                  boolean isCallId,
                                                  Map<String, List<HorizonWorkProgramme>> readOnlyMap) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(user.getId(), application.getId()).getSuccess();

        boolean readOnly = !readOnlyMap.isEmpty();

        List<ProcessRoleResource> processRoles = processRoleRestService.findProcessRole(applicationId).getSuccess();
        List<ProcessRoleResource> applicantProcessRoles = processRoles
                .stream()
                .filter(role -> applicantProcessRoles().contains(role.getRole()))
                .collect(toList());

        boolean leadApplicant = applicantProcessRoles.stream()
                .anyMatch(pr -> pr.getUser().equals(user.getId()) && pr.getRole() == LEADAPPLICANT);

        boolean allReadOnly = !leadApplicant;

        return new HorizonWorkProgrammeViewModel(
                application.getName(),
                applicationId,
                pageTitle,
                isCallId,
                questionId,
                allReadOnly,
                getLeadApplicantName(applicationId),
                isComplete(application, organisation, questionId),
                true,
                leadApplicant,
                readOnly,
                readOnlyMap
        );
    }

    private String getLeadApplicantName(long applicationId) {
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRole(applicationId);
        UserResource user = userRestService.retrieveUserById(leadApplicantProcessRole.getUser()).getSuccess();
        return user.getName();
    }

    private boolean isComplete(ApplicationResource application, OrganisationResource organisation, long questionId) {
        try {
            return questionStatusRestService.getMarkedAsComplete(application.getId(), organisation.getId()).get().contains(questionId);
        } catch (InterruptedException | ExecutionException e) {
            throw new IFSRuntimeException(e);
        }
    }

    private String getYourFinancesUrl(long applicationId, long organisationId, boolean applicant) {
        return applicant ?
                String.format("/application/%d/form/FINANCE", applicationId) :
                String.format("/application/%d/form/FINANCE/%d", applicationId, organisationId);
    }
}
