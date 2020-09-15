package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.mapper.FormInputResponseMapper;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.application.resource.FormInputResponseCommand;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.MultipleChoiceOption;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.PROJECT_SUMMARY;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.Role.applicantProcessRoles;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FormInputResponseServiceImplTest extends BaseServiceUnitTest<FormInputResponseServiceImpl> {

    @Mock
    private FormInputResponseRepository formInputResponseRepositoryMock;

    @Mock
    private FormInputResponseMapper formInputResponseMapperMock;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private ProcessRoleRepository processRoleRepositoryMock;

    @Mock
    private FormInputRepository formInputRepositoryMock;
    
    @Captor
    private ArgumentCaptor<FormInputResponse> formInputResponseArgumentCaptor;

    @Test
    public void findResponsesByFormInputIdAndQuestionSetupType() throws Exception {
        long applicationId = 1L;
        QuestionSetupType questionSetupType = PROJECT_SUMMARY;

        FormInputResponse formInputResponse = newFormInputResponse().build();
        FormInputResponseResource formInputResponseResource = newFormInputResponseResource().build();

        when(formInputResponseRepositoryMock.findOneByApplicationIdAndFormInputQuestionQuestionSetupType(applicationId, questionSetupType))
                .thenReturn(formInputResponse);

        when(formInputResponseMapperMock.mapToResource(formInputResponse)).thenReturn(formInputResponseResource);

        ServiceResult<FormInputResponseResource> serviceResult = service.findResponseByApplicationIdAndQuestionSetupType(applicationId, questionSetupType);

        assertTrue(serviceResult.isSuccess());
        assertEquals(formInputResponseResource, serviceResult.getSuccess());

        verify(formInputResponseRepositoryMock, only()).findOneByApplicationIdAndFormInputQuestionQuestionSetupType(applicationId,
                questionSetupType);
    }

    @Test
    public void findResponseByApplicationIdAndQuestionId() {
        long applicationId = 1L;
        long questionId = 2L;

        List<FormInputResponse> responses = newFormInputResponse().build(2);
        List<FormInputResponseResource> responseResources = newFormInputResponseResource().build(2);

        when(formInputResponseRepositoryMock.findByApplicationIdAndFormInputQuestionId(applicationId, questionId))
                .thenReturn(responses);

        when(formInputResponseMapperMock.mapToResource(responses.get(0))).thenReturn(responseResources.get(0));
        when(formInputResponseMapperMock.mapToResource(responses.get(1))).thenReturn(responseResources.get(1));

        List<FormInputResponseResource> serviceResult = service.findResponseByApplicationIdAndQuestionId(applicationId, questionId).getSuccess();

        assertEquals(responseResources, serviceResult);

        verify(formInputResponseRepositoryMock, only()).findByApplicationIdAndFormInputQuestionId(applicationId, questionId);
    }

    @Test
    public void saveQuestionResponse() {
        final long formInputId = 1234L;
        final long applicationId = 5921L;
        final long userId = 9523L;
        final String value = "<html>This is my html saving</html>";
        final FormInputResponseCommand formInputResponseCommand = new FormInputResponseCommand(formInputId, applicationId, userId, value, null);

        when(processRoleRepositoryMock.findOneByUserIdAndRoleInAndApplicationId(userId, applicantProcessRoles(), applicationId)).thenReturn(newProcessRole().build());
        when(userRepositoryMock.findById(userId)).thenReturn(Optional.of(newUser().withId(userId).withFirstName("Test").withLastName("User").build()));
        when(formInputRepositoryMock.findById(formInputId)).thenReturn(Optional.of(newFormInput().withId(formInputId).withType(FormInputType.TEXTAREA).withQuestion(newQuestion().withMultipleStatuses(Boolean.FALSE).build()).build()));
        when(applicationRepositoryMock.findById(applicationId)).thenReturn(Optional.of(newApplication().with(application -> application.setFormInputResponses(new ArrayList<FormInputResponse>()))
                .withCompetition(newCompetition().withCompetitionStatus(CompetitionStatus.OPEN).build()).build()));

        service.saveQuestionResponse(formInputResponseCommand);

        verify(applicationRepositoryMock, times(1)).save(any(Application.class));
        verify(formInputResponseRepositoryMock, times(1)).save(formInputResponseArgumentCaptor.capture());

        List<FormInputResponse> formInputResponses = formInputResponseArgumentCaptor.getAllValues();
        assertEquals(1, formInputResponses.size());
        assertEquals(value, formInputResponses.get(0).getValue());
        assertNull(formInputResponses.get(0).getMultipleChoiceOption());
    }

    @Test
    public void saveQuestionResponseForMultipleChoiceOptions() {
        final long formInputId = 1234L;
        final long applicationId = 5921L;
        final long userId = 9523L;
        final long multipleChoiceOptionId = 1L;
        final String multipleChoiceOptionText = "Yes";
        final FormInput formInput = newFormInput().withId(formInputId).withType(FormInputType.MULTIPLE_CHOICE)
                .withQuestion(newQuestion().withMultipleStatuses(Boolean.FALSE).build()).build();

        MultipleChoiceOption multipleChoiceOption = new MultipleChoiceOption(multipleChoiceOptionText, formInput);
        multipleChoiceOption.setId(multipleChoiceOptionId);

        final FormInputResponseCommand formInputResponseCommand = new FormInputResponseCommand(formInputId, applicationId, userId, multipleChoiceOptionText, multipleChoiceOptionId);

        when(processRoleRepositoryMock.findOneByUserIdAndRoleInAndApplicationId(userId, applicantProcessRoles(), applicationId)).thenReturn(newProcessRole().build());
        when(userRepositoryMock.findById(userId)).thenReturn(Optional.of(newUser().withId(userId).withFirstName("Test").withLastName("User").build()));
        when(formInputRepositoryMock.findById(formInputId)).thenReturn(Optional.of(formInput));
        when(applicationRepositoryMock.findById(applicationId)).thenReturn(Optional.of(newApplication().with(application -> application.setFormInputResponses(new ArrayList<FormInputResponse>()))
                .withCompetition(newCompetition().withCompetitionStatus(CompetitionStatus.OPEN).build()).build()));

        service.saveQuestionResponse(formInputResponseCommand);

        verify(applicationRepositoryMock, times(1)).save(any(Application.class));
        verify(formInputResponseRepositoryMock, times(1)).save(formInputResponseArgumentCaptor.capture());

        List<FormInputResponse> formInputResponses = formInputResponseArgumentCaptor.getAllValues();
        assertEquals(1, formInputResponses.size());
        assertEquals(multipleChoiceOptionText, formInputResponses.get(0).getValue());
        assertEquals(multipleChoiceOption, formInputResponses.get(0).getMultipleChoiceOption());
    }

    @Test
    public void findResponseByApplicationIdQuestionIdOrganisationIdAndFormInputType() {
        Application application = newApplication().build();
        Question question = newQuestion().build();
        Organisation organisation = newOrganisation().build();
        FormInputType formInputType = FormInputType.TEXTAREA;
        FormInputResponse formInputResponse = newFormInputResponse().build();
        FormInputResponseResource formInputResponseResource = newFormInputResponseResource().build();

        when(formInputResponseRepositoryMock
                        .findByApplicationIdAndFormInputQuestionIdAndUpdatedByOrganisationIdAndFormInputType(application.getId(), question.getId(), organisation.getId(), formInputType))
                .thenReturn(Optional.of(formInputResponse));
        when(formInputResponseMapperMock.mapToResource(formInputResponse)).thenReturn(formInputResponseResource);

        FormInputResponseResource actual =
                service.findResponseByApplicationIdQuestionIdOrganisationIdAndFormInputType(application.getId(), question.getId(), organisation.getId(), formInputType).getSuccess();

        assertEquals(formInputResponseResource, actual);

        verify(formInputResponseRepositoryMock, only())
                .findByApplicationIdAndFormInputQuestionIdAndUpdatedByOrganisationIdAndFormInputType(application.getId(), question.getId(), organisation.getId(), formInputType);
        verify(formInputResponseMapperMock, only()).mapToResource(formInputResponse);
    }

    @Test
    public void findResponseByApplicationIdQuestionIdOrganisationIdFormInputTypeAndDescription() {
        Application application = newApplication().build();
        Question question = newQuestion().build();
        Organisation organisation = newOrganisation().build();
        FormInputType formInputType = FormInputType.TEXTAREA;
        String description = "description";
        FormInputResponse formInputResponse = newFormInputResponse().build();
        FormInputResponseResource formInputResponseResource = newFormInputResponseResource().build();

        when(formInputResponseRepositoryMock
                .findByApplicationIdAndFormInputQuestionIdAndUpdatedByOrganisationIdAndFormInputTypeAndFormInputDescription(application.getId(), question.getId(), organisation.getId(), formInputType, description))
                .thenReturn(Optional.of(formInputResponse));
        when(formInputResponseMapperMock.mapToResource(formInputResponse)).thenReturn(formInputResponseResource);

        FormInputResponseResource actual =
                service.findResponseByApplicationIdQuestionIdOrganisationIdFormInputTypeAndDescription(application.getId(), question.getId(), organisation.getId(), formInputType, description).getSuccess();

        assertEquals(formInputResponseResource, actual);

        verify(formInputResponseRepositoryMock, only())
                .findByApplicationIdAndFormInputQuestionIdAndUpdatedByOrganisationIdAndFormInputTypeAndFormInputDescription(application.getId(), question.getId(), organisation.getId(), formInputType, description);
        verify(formInputResponseMapperMock, only()).mapToResource(formInputResponse);
    }

    @Override
    protected FormInputResponseServiceImpl supplyServiceUnderTest() {
        return new FormInputResponseServiceImpl();
    }
}