package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.application.domain.QuestionStatus;
import org.innovateuk.ifs.application.repository.QuestionStatusRepository;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Service containing logic for question reassignment upon invite deletion.
 */
@Service
public class QuestionReassignmentServiceImpl implements QuestionReassignmentService {

    @Autowired
    protected ProcessRoleRepository processRoleRepository;

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private QuestionStatusRepository questionStatusRepository;

    @Override
    public void reassignCollaboratorResponsesAndQuestionStatuses(Long applicationId,  List<ProcessRole> collaboratorProcessRoles, ProcessRole leadApplicantProcessRole) {
        collaboratorProcessRoles.forEach(collaboratorProcessRole -> {
            List<ProcessRole> organisationRoles = getOrganisationProcessRolesExcludingCollaborator(applicationId, collaboratorProcessRole);

            reassignCollaboratorFormResponses(leadApplicantProcessRole, collaboratorProcessRole, organisationRoles);
            reassignCollaboratorQuestionStatuses(applicationId, leadApplicantProcessRole, collaboratorProcessRole, organisationRoles);
        });
    }

    private List<ProcessRole> getOrganisationProcessRolesExcludingCollaborator(long applicationId, ProcessRole collaboratorProcessRole) {
        List<ProcessRole> organisationRoles = processRoleRepository.findByApplicationIdAndOrganisationId(applicationId, collaboratorProcessRole.getOrganisationId());
        organisationRoles.remove(collaboratorProcessRole);
        return organisationRoles;
    }

    private void reassignCollaboratorFormResponses(ProcessRole leadApplicantProcessRole,
                                                   ProcessRole collaboratorProcessRole,
                                                   List<ProcessRole> organisationRoles) {
        List<FormInputResponse> formInputResponses = formInputResponseRepository.findByUpdatedById(collaboratorProcessRole.getId());

        List<FormInputResponse> unassignableFormInputResponses = new ArrayList<>();

        formInputResponses.forEach(collaboratorResponse -> {
            if (collaboratorResponse.getFormInput().getQuestion().hasMultipleStatuses()) {
                if (organisationRoles.isEmpty()) {
                    unassignableFormInputResponses.add(collaboratorResponse);
                } else {
                    collaboratorResponse.setUpdatedBy(organisationRoles.get(0));
                }
            } else {
                collaboratorResponse.setUpdatedBy(leadApplicantProcessRole);
            }
        });

        formInputResponseRepository.saveAll(formInputResponses);
        formInputResponseRepository.deleteAll(unassignableFormInputResponses);
    }

    private void reassignCollaboratorQuestionStatuses(long applicationId,
                                                      ProcessRole leadApplicantProcessRole,
                                                      ProcessRole collaboratorProcessRole,
                                                      List<ProcessRole> organisationRoles) {
        List<QuestionStatus> questionStatuses = questionStatusRepository.findByApplicationIdAndMarkedAsCompleteByIdOrAssigneeIdOrAssignedById(
                applicationId,
                collaboratorProcessRole.getId(),
                collaboratorProcessRole.getId(),
                collaboratorProcessRole.getId()
        );

        List<QuestionStatus> unassignableQuestionStatuses = new ArrayList<>();

        questionStatuses.forEach(questionStatus -> {
            if (questionStatus.getQuestion().hasMultipleStatuses()) {
                if (organisationRoles.isEmpty()) {
                    unassignableQuestionStatuses.add(questionStatus);
                } else {
                    reassignQuestionStatusRoles(questionStatus, organisationRoles.get(0), leadApplicantProcessRole);
                }
            } else {
                reassignQuestionStatusRoles(questionStatus, leadApplicantProcessRole, leadApplicantProcessRole);
            }
        });

        questionStatusRepository.saveAll(questionStatuses);
        questionStatusRepository.deleteAll(unassignableQuestionStatuses);
    }

    private static QuestionStatus reassignQuestionStatusRoles(QuestionStatus questionStatus, ProcessRole reassignTo, ProcessRole leadApplicantRole) {
        if (questionStatus.getAssignee() != null && questionStatus.getAssignedBy() != null) {
            ProcessRole assignee =
                    convertToProcessRoleIfOriginalRoleNotForLeadApplicant(questionStatus.getAssignee(), reassignTo, leadApplicantRole);
            ProcessRole assignedBy =
                    convertToProcessRoleIfOriginalRoleNotForLeadApplicant(questionStatus.getAssignedBy(), reassignTo, leadApplicantRole);

            questionStatus.setAssignee(assignee, assignedBy, ZonedDateTime.now());
        }

        if (questionStatus.getMarkedAsCompleteBy() != null) {
            ProcessRole markedAsCompleteBy =
                    convertToProcessRoleIfOriginalRoleNotForLeadApplicant(questionStatus.getMarkedAsCompleteBy(), reassignTo, leadApplicantRole);

            questionStatus.setMarkedAsCompleteBy(markedAsCompleteBy);
        }

        return questionStatus;
    }

    private static ProcessRole convertToProcessRoleIfOriginalRoleNotForLeadApplicant(ProcessRole processRoleFrom,
                                                                              ProcessRole processRoleTo,
                                                                              ProcessRole leadApplicantRole) {
        return Optional.of(processRoleFrom)
                .filter(originalRole -> !originalRole.getId().equals(leadApplicantRole.getId()))
                .map(originalRole -> processRoleTo)
                .orElse(leadApplicantRole);
    }
}
