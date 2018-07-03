package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.domain.QuestionStatus;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.application.repository.QuestionStatusRepository;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.application.builder.QuestionStatusBuilder.newQuestionStatus;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.Role.COLLABORATOR;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public class QuestionReassignmentServiceImplTest {

    private List<ProcessRole> inviteProcessRoles;
    private ProcessRole leadApplicantProcessRole;

    @Mock
    private ProcessRoleRepository processRoleRepositoryMock;

    @Mock
    private FormInputResponseRepository formInputResponseRepositoryMock;

    @Mock
    private QuestionStatusRepository questionStatusRepositoryMock;

    @InjectMocks
    private QuestionReassignmentService questionReassignmentService = new QuestionReassignmentServiceImpl();

    @Before
    public void setupMockInjection() {
        MockitoAnnotations.initMocks(this);
    }

    @Before
    public void setupLeadAndCollaboratorUserProcessRoles() {
        inviteProcessRoles = newProcessRole()
                .withRole(COLLABORATOR)
                .withUser(newUser().build())
                .withOrganisationId(1L)
                .build(1);

        leadApplicantProcessRole = newProcessRole()
                .withRole(Role.LEADAPPLICANT)
                .withUser(newUser().build())
                .build();
    }

    @Test
    public void reassignCollaboratorResponsesAndQuestionStatuses_reassignsQuestionsToLeadApplicantWhenLastAssignableUser() throws Exception {
        User inviteUser = inviteProcessRoles.get(0).getUser();
        Application application = newApplication().withProcessRoles(leadApplicantProcessRole, inviteProcessRoles.get(0)).build();
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

        when(processRoleRepositoryMock.findByUserAndApplicationId(inviteUser, application.getId())).thenReturn(inviteProcessRoles);
        when(processRoleRepositoryMock.findByApplicationIdAndOrganisationId(application.getId(), inviteProcessRoles.get(0).getOrganisationId()))
                .thenReturn(newArrayList(inviteProcessRoles.get(0)));
        when(formInputResponseRepositoryMock.findByUpdatedById(inviteProcessRoles.get(0).getId())).thenReturn(inviteResponses);
        when(questionStatusRepositoryMock.findByApplicationIdAndMarkedAsCompleteByIdOrAssigneeIdOrAssignedById(
                application.getId(),
                inviteProcessRoles.get(0).getId(),
                inviteProcessRoles.get(0).getId(),
                inviteProcessRoles.get(0).getId()
        )).thenReturn(questionStatuses);

        questionReassignmentService.reassignCollaboratorResponsesAndQuestionStatuses(application.getId(), inviteProcessRoles, leadApplicantProcessRole);

        InOrder inOrder = inOrder(formInputResponseRepositoryMock, questionStatusRepositoryMock);

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
        inOrder.verify(questionStatusRepositoryMock).delete(anyCollectionOf(QuestionStatus.class));

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void reassignCollaboratorResponsesAndQuestionStatuses_reassignsQuestionsToOtherOrganisationMemberWhenAnotherOrganisationMemberIsAssignable() {
        User inviteUser = inviteProcessRoles.get(0).getUser();

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

        questionReassignmentService.reassignCollaboratorResponsesAndQuestionStatuses(application.getId(), inviteProcessRoles, leadApplicantProcessRole);

        InOrder inOrder = inOrder(formInputResponseRepositoryMock, questionStatusRepositoryMock);

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
        inOrder.verify(questionStatusRepositoryMock).delete(anyCollectionOf(QuestionStatus.class));

        inOrder.verifyNoMoreInteractions();
    }

}