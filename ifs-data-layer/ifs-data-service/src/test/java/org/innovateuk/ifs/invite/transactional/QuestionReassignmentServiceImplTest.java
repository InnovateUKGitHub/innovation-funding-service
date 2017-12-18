package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.QuestionStatus;
import org.innovateuk.ifs.application.repository.QuestionStatusRepository;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.repository.FormInputResponseRepository;
import org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.mapper.ApplicationInviteMapper;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.application.builder.QuestionStatusBuilder.newQuestionStatus;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newApplicationInvite;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.UserRoleType.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public class QuestionReassignmentServiceImplTest {
    @Mock
    private ApplicationInviteMapper applicationInviteMapper;

    @Mock
    private ProcessRoleRepository processRoleRepositoryMock;

    @Mock
    private FormInputResponseRepository formInputResponseRepositoryMock;

    @Mock
    private QuestionStatusRepository questionStatusRepositoryMock;

    @Mock
    private InviteOrganisationRepository inviteOrganisationRepositoryMock;

    @InjectMocks
    private QuestionReassignmentService inviteService = new QuestionReassignmentServiceImpl();

    @Test
    public void reassignCollaboratorResponsesAndQuestionStatuses_reassignsQuestionsToLeadApplicant() throws Exception {
        User inviteUser = newUser().build();
        List<ProcessRole> inviteProcessRoles = newProcessRole()
                .withRole(newRole(COLLABORATOR))
                .withUser(inviteUser)
                .withOrganisationId(1L)
                .build(1);

        ProcessRole leadApplicantProcessRole = newProcessRole()
                .withRole(newRole(LEADAPPLICANT))
                .withUser(newUser().build())
                .build();

        Application application = newApplication()
                .withProcessRoles(leadApplicantProcessRole, inviteProcessRoles.get(0))
                .build();

        Question question = newQuestion().withMultipleStatuses(false).build();

        List<FormInputResponse> inviteResponses = newFormInputResponse()
                .withFormInputs(
                        newFormInput().withQuestion(question).build(1)
                )
                .withUpdatedBy(inviteProcessRoles.get(0))
                .withValue("Test value")
                .build(2);
        List<QuestionStatus> questionStatuses = newQuestionStatus()
                .withApplication(application)
                .withQuestion(question)
                .withMarkedAsCompleteBy(inviteProcessRoles.get(0))
                .withAssignee(inviteProcessRoles.get(0))
                .withAssignedBy(inviteProcessRoles.get(0))
                .withMarkedAsComplete(true)
                .build(2);

        Long inviteProcessRoleId = inviteProcessRoles.get(0).getId();

        when(processRoleRepositoryMock.findByUserAndApplicationId(inviteUser, application.getId())).thenReturn(inviteProcessRoles);
        when(processRoleRepositoryMock.findByApplicationIdAndOrganisationId(application.getId(), inviteProcessRoles.get(0).getOrganisationId()))
                .thenReturn(newArrayList(inviteProcessRoles.get(0)));

        when(formInputResponseRepositoryMock.findByUpdatedById(inviteProcessRoleId)).thenReturn(inviteResponses);

        when(questionStatusRepositoryMock.findByApplicationIdAndMarkedAsCompleteByIdOrAssigneeIdOrAssignedById(
                application.getId(),
                inviteProcessRoleId,
                inviteProcessRoleId,
                inviteProcessRoleId
        ))
                .thenReturn(questionStatuses);

        inviteService.reassignCollaboratorResponsesAndQuestionStatuses(application.getId(), inviteProcessRoles, leadApplicantProcessRole);

        InOrder inOrder = inOrder(formInputResponseRepositoryMock, questionStatusRepositoryMock, processRoleRepositoryMock, inviteOrganisationRepositoryMock);

        inOrder.verify(formInputResponseRepositoryMock).save(
                createLambdaMatcher((List<FormInputResponse> actual) ->
                        actual.stream().allMatch(fir -> fir.getUpdatedBy().equals(leadApplicantProcessRole))
                )
        );
        inOrder.verify(questionStatusRepositoryMock).save(
                createLambdaMatcher((List<QuestionStatus> actual) ->
                        actual.stream().allMatch(qs -> qs.getMarkedAsCompleteBy().equals(leadApplicantProcessRole) &&
                                qs.getAssignedBy().equals(leadApplicantProcessRole) &&
                                qs.getAssignee().equals(leadApplicantProcessRole))
                )
        );
        inOrder.verify(processRoleRepositoryMock).delete(inviteProcessRoles);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void reassignCollaboratorResponsesAndQuestionStatuses_reassignsQuestionsToOtherOrganisationMember() throws Exception {
        User inviteUser = newUser().build();
        List<ProcessRole> inviteProcessRoles = newProcessRole()
                .withRole(COLLABORATOR)
                .withUser(inviteUser)
                .withOrganisationId(1L)
                .build(1);

        ProcessRole leadApplicantProcessRole = newProcessRole()
                .withRole(LEADAPPLICANT)
                .withUser(newUser().build())
                .build();

        List<ProcessRole> organisationProcessRoles = newArrayList(
                newProcessRole()
                        .withId(10L)
                        .withRole(COLLABORATOR)
                        .build(),
                inviteProcessRoles.get(0),
                newProcessRole()
                        .withId(11L)
                        .withRole(COLLABORATOR)
                        .build()
        );

        Application application = newApplication()
                .withProcessRoles(leadApplicantProcessRole, inviteProcessRoles.get(0))
                .build();
        ApplicationInvite applicationInvite = newApplicationInvite()
                .withId(12L)
                .withUser(inviteUser)
                .withApplication(application)
                .withInviteOrganisation(
                        newInviteOrganisation()
                                .withInvites(ApplicationInviteBuilder.newApplicationInvite().build(2))
                                .build())
                .build();

        Question question = newQuestion().withMultipleStatuses(true).build();

        List<FormInputResponse> inviteResponses = newFormInputResponse()
                .withFormInputs(
                        newFormInput().withQuestion(question).build(1)
                )
                .withUpdatedBy(inviteProcessRoles.get(0))
                .withValue("Test value")
                .build(2);
        List<QuestionStatus> questionStatuses = newQuestionStatus()
                .withApplication(application)
                .withQuestion(question)
                .withAssignedBy(inviteProcessRoles.get(0))
                .withAssignee(leadApplicantProcessRole)
                .withMarkedAsCompleteBy(inviteProcessRoles.get(0))
                .withMarkedAsComplete(true)
                .build(2);

        Long inviteProcessRoleId = inviteProcessRoles.get(0).getId();

        when(applicationInviteMapper.mapIdToDomain(applicationInvite.getId())).thenReturn(applicationInvite);
        when(processRoleRepositoryMock.findByUserAndApplicationId(inviteUser, application.getId())).thenReturn(inviteProcessRoles);
        when(processRoleRepositoryMock.findByApplicationIdAndOrganisationId(application.getId(), inviteProcessRoles.get(0).getOrganisationId()))
                .thenReturn(organisationProcessRoles);

        when(formInputResponseRepositoryMock.findByUpdatedById(inviteProcessRoleId)).thenReturn(inviteResponses);

        when(questionStatusRepositoryMock.findByApplicationIdAndMarkedAsCompleteByIdOrAssigneeIdOrAssignedById(
                application.getId(),
                inviteProcessRoleId,
                inviteProcessRoleId,
                inviteProcessRoleId
        )).thenReturn(questionStatuses);

        //ServiceResult<Void> serviceResult = inviteService.removeApplicationInvite(applicationInvite.getId());

        InOrder inOrder = inOrder(formInputResponseRepositoryMock, questionStatusRepositoryMock, processRoleRepositoryMock, inviteOrganisationRepositoryMock);

        inOrder.verify(formInputResponseRepositoryMock).save(
                createLambdaMatcher((List<FormInputResponse> actual) ->
                        actual.stream().allMatch(fir -> fir.getUpdatedBy().equals(organisationProcessRoles.get(0)))
                )
        );
        inOrder.verify(questionStatusRepositoryMock).save(
                createLambdaMatcher((List<QuestionStatus> actual) ->
                        actual.stream().allMatch(qs -> qs.getMarkedAsCompleteBy().equals(organisationProcessRoles.get(0)) &&
                                qs.getAssignedBy().equals(organisationProcessRoles.get(0)) &&
                                qs.getAssignee().equals(leadApplicantProcessRole)
                        )
                )
        );
        inOrder.verify(processRoleRepositoryMock).delete(inviteProcessRoles);
        inOrder.verify(inviteOrganisationRepositoryMock).save(applicationInvite.getInviteOrganisation());

        inOrder.verifyNoMoreInteractions();
    }

}