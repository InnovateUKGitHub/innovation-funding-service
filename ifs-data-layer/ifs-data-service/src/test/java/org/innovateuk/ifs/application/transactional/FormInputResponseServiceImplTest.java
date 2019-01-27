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
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Test;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
        final FormInputResponseCommand formInputResponseCommand = new FormInputResponseCommand(formInputId, applicationId, userId, value);

        when(processRoleRepositoryMock.findOneByUserIdAndRoleInAndApplicationId(userId, applicantProcessRoles(), applicationId)).thenReturn(newProcessRole().build());
        when(userRepositoryMock.findOne(userId)).thenReturn(newUser().withId(userId).withFirstName("Test").withLastName("User").build());
        when(formInputRepositoryMock.findOne(formInputId)).thenReturn(newFormInput().withId(formInputId).withQuestion(newQuestion().withMultipleStatuses(Boolean.FALSE).build()).build());
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(newApplication().with(application -> application.setFormInputResponses(new ArrayList<FormInputResponse>()))
                .withCompetition(newCompetition().withCompetitionStatus(CompetitionStatus.OPEN).build()).build());

        service.saveQuestionResponse(formInputResponseCommand);

        verify(applicationRepositoryMock, times(1)).save(any(Application.class));
        verify(formInputResponseRepositoryMock, times(1)).save(any(FormInputResponse.class));
    }

    @Test
    public void findResponseByApplicationIdQuestionIdOrganisationIdAndFormInputType() {
        Application application = newApplication().build();
        Question question = newQuestion().build();
        Organisation organisation = newOrganisation().build();
        FormInputType formInputType = FormInputType.FINANCE;
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
        FormInputType formInputType = FormInputType.FINANCE;
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