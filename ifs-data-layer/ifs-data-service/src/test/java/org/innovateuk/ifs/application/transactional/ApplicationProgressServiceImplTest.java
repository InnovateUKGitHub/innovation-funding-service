package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.form.builder.QuestionBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.OrganisationType;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.application.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ApplicationProgressServiceImplTest {

    @Mock
    private QuestionService questionServiceMock;

    @Mock
    private QuestionStatusService questionStatusServiceMock;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private SectionService sectionServiceMock;

    @Mock
    private SectionStatusService sectionStatusServiceMock;

    @Mock
    private ApplicationFinanceHandler applicationFinanceHandlerMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @InjectMocks
    private ApplicationProgressService service = new ApplicationProgressServiceImpl();

    private FormInput formInput;
    private FormInputType formInputType;
    private Question question;
    private FileEntryResource fileEntryResource;
    private FormInputResponseFileEntryResource formInputResponseFileEntryResource;
    private FileEntry existingFileEntry;
    private FormInputResponse existingFormInputResponse;
    private List<FormInputResponse> existingFormInputResponses;
    private FormInputResponse unlinkedFormInputFileEntry;
    private Long organisationId = 456L;

    private Question multiAnswerQuestion;
    private Question leadAnswerQuestion;

    private OrganisationType orgType;
    private Organisation org1;
    private Organisation org2;
    private Organisation org3;

    private ProcessRole[] roles;
    private Section section;
    private Competition comp;
    private Application app;

    private Application openApplication;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        question = QuestionBuilder.newQuestion().build();

        formInputType = FormInputType.FILEUPLOAD;

        formInput = newFormInput().withType(formInputType).build();
        formInput.setId(123L);
        formInput.setQuestion(question);
        question.setFormInputs(singletonList(formInput));

        fileEntryResource = newFileEntryResource().with(id(999L)).build();
        formInputResponseFileEntryResource = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);

        existingFileEntry = newFileEntry().with(id(999L)).build();
        existingFormInputResponse = newFormInputResponse().withFileEntry(existingFileEntry).build();
        existingFormInputResponses = singletonList(existingFormInputResponse);
        unlinkedFormInputFileEntry = newFormInputResponse().with(id(existingFormInputResponse.getId())).withFileEntry(null).build();
        final Competition openCompetition = newCompetition().withCompetitionStatus(CompetitionStatus.OPEN).build();
        openApplication = newApplication().withCompetition(openCompetition).build();

        when(applicationRepositoryMock.findOne(anyLong())).thenReturn(openApplication);

        multiAnswerQuestion = newQuestion().withMarksAsCompleteEnabled(Boolean.TRUE).withMultipleStatuses(Boolean.TRUE).withId(123L).build();
        leadAnswerQuestion = newQuestion().withMarksAsCompleteEnabled(Boolean.TRUE).withMultipleStatuses(Boolean.FALSE).withId(321L).build();

        orgType = newOrganisationType().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        org1 = newOrganisation().withOrganisationType(orgType).withId(234L).build();
        org2 = newOrganisation().withId(345L).build();
        org3 = newOrganisation().withId(456L).build();

        roles = newProcessRole().withRole(Role.LEADAPPLICANT, Role.APPLICANT, Role.COLLABORATOR).withOrganisationId(234L, 345L, 456L).build(3).toArray(new ProcessRole[0]);
        section = newSection().withQuestions(Arrays.asList(multiAnswerQuestion, leadAnswerQuestion)).build();
        comp = newCompetition().withSections(Arrays.asList(section)).withMaxResearchRatio(30).build();
        app = newApplication().withCompetition(comp).withProcessRoles(roles).build();

        when(applicationRepositoryMock.findOne(app.getId())).thenReturn(app);
        when(organisationRepositoryMock.findOne(234L)).thenReturn(org1);
        when(organisationRepositoryMock.findOne(345L)).thenReturn(org2);
        when(organisationRepositoryMock.findOne(456L)).thenReturn(org3);
    }
    @Test
    public void updateApplicationProgress_notCompletedAllSingleStatusQuestions() {
        List<Question> questions = newQuestion()
                .withMarksAsCompleteEnabled(true)
                .withMultipleStatuses(false)
                .build(2);

        setQuestionsOnApplication(questions);

        when(questionStatusServiceMock.isMarkedAsComplete(questions.get(0), app.getId(), 0L))
                .thenReturn(serviceSuccess(true));
        when(questionStatusServiceMock.isMarkedAsComplete(questions.get(1), app.getId(), 0L))
                .thenReturn(serviceSuccess(false));

        ServiceResult<BigDecimal> result = service.updateApplicationProgress(app.getId());

        verify(applicationRepositoryMock).findOne(app.getId());

        assertTrue(result.isSuccess());
        assertEquals(BigDecimal.valueOf(50).setScale(2, RoundingMode.UNNECESSARY), result.getSuccess());
    }

    @Test
    public void getApplicationReadyToSubmit() throws Exception {

        when(questionStatusServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org1.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionStatusServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org2.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionStatusServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org3.getId())).thenReturn(serviceSuccess(Boolean.TRUE));

        when(questionStatusServiceMock.isMarkedAsComplete(leadAnswerQuestion, app.getId(), 0L)).thenReturn(serviceSuccess(Boolean.TRUE));
        when(sectionStatusServiceMock.childSectionsAreCompleteForAllOrganisations(null, app.getId(), null)).thenReturn(serviceSuccess(Boolean.TRUE));
        when(applicationFinanceHandlerMock.getResearchParticipationPercentage(app.getId())).thenReturn(new BigDecimal("29"));

        boolean result = service.applicationReadyForSubmit(app.getId());
        assertTrue(result);
    }

    @Test
    public void applicationNotReadyToSubmitResearchParticipationTooHigh() throws Exception {

        when(questionStatusServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org1.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionStatusServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org2.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionStatusServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org3.getId())).thenReturn(serviceSuccess(Boolean.TRUE));

        when(questionStatusServiceMock.isMarkedAsComplete(leadAnswerQuestion, app.getId(), 0L)).thenReturn(serviceSuccess(Boolean.TRUE));
        when(sectionStatusServiceMock.childSectionsAreCompleteForAllOrganisations(null, app.getId(), null)).thenReturn(serviceSuccess(Boolean.TRUE));
        when(applicationFinanceHandlerMock.getResearchParticipationPercentage(app.getId())).thenReturn(new BigDecimal("31"));

        boolean result = service.applicationReadyForSubmit(app.getId());
        assertFalse(result);
    }

    @Test
    public void applicationNotReadyToSubmitProgressNotComplete() throws Exception {

        when(questionStatusServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org1.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionStatusServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org2.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionStatusServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org3.getId())).thenReturn(serviceSuccess(Boolean.FALSE));

        when(questionStatusServiceMock.isMarkedAsComplete(leadAnswerQuestion, app.getId(), 0L)).thenReturn(serviceSuccess(Boolean.TRUE));
        when(sectionStatusServiceMock.childSectionsAreCompleteForAllOrganisations(null, app.getId(), null)).thenReturn(serviceSuccess(Boolean.TRUE));

        boolean result = service.applicationReadyForSubmit(app.getId());
        assertFalse(result);
    }

    @Test
    public void applicationNotReadyToSubmitChildSectionsNotComplete() throws Exception {

        when(questionStatusServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org1.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionStatusServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org2.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionStatusServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org3.getId())).thenReturn(serviceSuccess(Boolean.TRUE));

        when(questionStatusServiceMock.isMarkedAsComplete(leadAnswerQuestion, app.getId(), 0L)).thenReturn(serviceSuccess(Boolean.TRUE));
        when(sectionStatusServiceMock.childSectionsAreCompleteForAllOrganisations(null, app.getId(), null)).thenReturn(serviceSuccess(Boolean.FALSE));

        boolean result = service.applicationReadyForSubmit(app.getId());
        assertFalse(result);
    }

    @Test
    public void updateApplicationProgress_completedAllSingleStatusQuestions() {
        List<Question> questions = newQuestion()
                .withMarksAsCompleteEnabled(true)
                .withMultipleStatuses(false)
                .build(2);

        setQuestionsOnApplication(questions);

        when(questionStatusServiceMock.isMarkedAsComplete(questions.get(0), app.getId(), 0L))
                .thenReturn(serviceSuccess(true));
        when(questionStatusServiceMock.isMarkedAsComplete(questions.get(1), app.getId(), 0L))
                .thenReturn(serviceSuccess(true));

        ServiceResult<BigDecimal> result = service.updateApplicationProgress(app.getId());

        verify(applicationRepositoryMock).findOne(app.getId());

        assertTrue(result.isSuccess());
        assertEquals(BigDecimal.valueOf(100).setScale(2, RoundingMode.UNNECESSARY), result.getSuccess());
    }

    private void setQuestionsOnApplication(List<Question> questions) {
        app.setCompetition(
                newCompetition()
                        .withSections(
                                newSection()
                                        .withQuestions(questions)
                                        .build(2)
                        )
                        .build()
        );
    }
}