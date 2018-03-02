package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.application.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ApplicationProgressServiceImplTest {

    @Mock
    private QuestionService questionServiceMock;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private SectionService sectionServiceMock;

    @Mock
    private ApplicationFinanceHandler applicationFinanceHandlerMock;

    @InjectMocks
    private ApplicationProgressService service = new ApplicationProgressServiceImpl();

    private Application app;
    private List<ProcessRole> roles;
    private Competition comp;
    private Section section;
    private Question multiAnswerQuestion;
    private Question leadAnswerQuestion;

    private Organisation org1;
    private Organisation org2;
    private Organisation org3;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        multiAnswerQuestion = newQuestion().withMarksAsCompleteEnabled(Boolean.TRUE).withMultipleStatuses(Boolean.TRUE).withId(123L).build();
        leadAnswerQuestion = newQuestion().withMarksAsCompleteEnabled(Boolean.TRUE).withMultipleStatuses(Boolean.FALSE).withId(321L).build();

        roles = newProcessRole()
                .withRole(UserRoleType.LEADAPPLICANT, UserRoleType.APPLICANT, UserRoleType.COLLABORATOR)
                .withOrganisationId(234L, 345L, 456L)
                .build(3);
        section = newSection().withQuestions(Arrays.asList(multiAnswerQuestion, leadAnswerQuestion)).build();
        app = newApplication().withCompetition(comp).withProcessRoles(roles.toArray(new ProcessRole[0])).build();

        org1 = newOrganisation().withOrganisationType(newOrganisationType().withOrganisationType(OrganisationTypeEnum.BUSINESS).build()).withId(234L).build();
        org2 = newOrganisation().withId(345L).build();
        org3 = newOrganisation().withId(456L).build();
    }

    @Test
    public void updateApplicationProgress_notCompletedAllSingleStatusQuestions() {
        List<Question> questions = newQuestion()
                .withMarksAsCompleteEnabled(true)
                .withMultipleStatuses(false)
                .build(2);

        setQuestionsOnApplication(questions);

        when(questionServiceMock.isMarkedAsComplete(questions.get(0), app.getId(), 0L))
                .thenReturn(serviceSuccess(true));
        when(questionServiceMock.isMarkedAsComplete(questions.get(1), app.getId(), 0L))
                .thenReturn(serviceSuccess(false));

        ServiceResult<BigDecimal> result = service.updateApplicationProgress(app.getId());

        verify(applicationRepositoryMock).findOne(app.getId());

        assertTrue(result.isSuccess());
        assertEquals(BigDecimal.valueOf(50).setScale(2, RoundingMode.UNNECESSARY), result.getSuccess());
    }

    @Test
    public void getApplicationReadyToSubmit() throws Exception {

        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org1.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org2.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org3.getId())).thenReturn(serviceSuccess(Boolean.TRUE));

        when(questionServiceMock.isMarkedAsComplete(leadAnswerQuestion, app.getId(), 0L)).thenReturn(serviceSuccess(Boolean.TRUE));
        when(sectionServiceMock.childSectionsAreCompleteForAllOrganisations(null, app.getId(), null)).thenReturn(serviceSuccess(Boolean.TRUE));
        when(applicationFinanceHandlerMock.getResearchParticipationPercentage(app.getId())).thenReturn(new BigDecimal("29"));

        boolean result = service.applicationReadyForSubmit(app.getId());
        assertTrue(result);
    }

    @Test
    public void applicationNotReadyToSubmitResearchParticipationTooHigh() throws Exception {

        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org1.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org2.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org3.getId())).thenReturn(serviceSuccess(Boolean.TRUE));

        when(questionServiceMock.isMarkedAsComplete(leadAnswerQuestion, app.getId(), 0L)).thenReturn(serviceSuccess(Boolean.TRUE));
        when(sectionServiceMock.childSectionsAreCompleteForAllOrganisations(null, app.getId(), null)).thenReturn(serviceSuccess(Boolean.TRUE));
        when(applicationFinanceHandlerMock.getResearchParticipationPercentage(app.getId())).thenReturn(new BigDecimal("31"));

        boolean result = service.applicationReadyForSubmit(app.getId());
        assertTrue(result);
    }

    @Test
    public void applicationNotReadyToSubmitProgressNotComplete() throws Exception {

        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org1.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org2.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org3.getId())).thenReturn(serviceSuccess(Boolean.FALSE));

        when(questionServiceMock.isMarkedAsComplete(leadAnswerQuestion, app.getId(), 0L)).thenReturn(serviceSuccess(Boolean.TRUE));
        when(sectionServiceMock.childSectionsAreCompleteForAllOrganisations(null, app.getId(), null)).thenReturn(serviceSuccess(Boolean.TRUE));

        boolean result = service.applicationReadyForSubmit(app.getId());
        assertFalse(result);
    }

    @Test
    public void applicationNotReadyToSubmitChildSectionsNotComplete() throws Exception {

        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org1.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org2.getId())).thenReturn(serviceSuccess(Boolean.TRUE));
        when(questionServiceMock.isMarkedAsComplete(multiAnswerQuestion, app.getId(), org3.getId())).thenReturn(serviceSuccess(Boolean.TRUE));

        when(questionServiceMock.isMarkedAsComplete(leadAnswerQuestion, app.getId(), 0L)).thenReturn(serviceSuccess(Boolean.TRUE));
        when(sectionServiceMock.childSectionsAreCompleteForAllOrganisations(null, app.getId(), null)).thenReturn(serviceSuccess(Boolean.FALSE));

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

        when(questionServiceMock.isMarkedAsComplete(questions.get(0), app.getId(), 0L))
                .thenReturn(serviceSuccess(true));
        when(questionServiceMock.isMarkedAsComplete(questions.get(1), app.getId(), 0L))
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