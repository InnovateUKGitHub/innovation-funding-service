package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.validation.ApplicationValidatorService;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.*;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class SectionStatusServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    protected SectionStatusService sectionStatusService = new SectionStatusServiceImpl();

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private QuestionStatusService questionStatusServiceMock;

    @Mock
    private ApplicationFinanceService financeServiceMock;

    @Mock
    private ApplicationValidatorService applicationValidatorService;

    @Test
    public void getCompletedSectionsMap() {

        long applicationId = 1L;
        long organisationId = 2L;
        long sectionId = 3L;

        ProcessRole processRole = newProcessRole()
                .withOrganisationId(organisationId)
                .withRole(Role.LEADAPPLICANT)
                .build();

        List<Question> questions = newQuestion()
                .withMultipleStatuses(true)
                .build(1);

        List<QuestionStatusResource> questionStatusResources = newQuestionStatusResource()
                .withMarkedAsComplete(true)
                .withMarkedAsCompleteByOrganisationId(organisationId)
                .withQuestion(questions.get(0).getId())
                .build(1);

        List<Section> sections = newSection()
                .withId(sectionId)
                .withSectionType(SectionType.FINANCE)
                .withQuestions(questions)
                .build(1);

        Competition competition = newCompetition()
                .withSections(sections)
                .withQuestions(questions)
                .build();

        Application application = newApplication()
                .withId(applicationId)
                .withCompetition(competition)
                .withProcessRoles(processRole)
                .build();

        Map<Long, List<QuestionStatusResource>> completedQuestionStatuses = new HashMap<>();
        completedQuestionStatuses.put(organisationId, questionStatusResources);

        when(applicationRepositoryMock.findById(applicationId)).thenReturn(Optional.of(application));
        when(questionStatusServiceMock.findCompletedQuestionsByApplicationId(applicationId)).thenReturn(serviceSuccess(questionStatusResources));

        Map<Long, Set<Long>> result = sectionStatusService.getCompletedSections(applicationId).getSuccess();

        assertTrue(result.get(organisationId).contains(sectionId));
    }

    @Test
    public void completeSections() {

        long applicationId = 1L;
        long organisationId = 2L;
        long financeSectionId = 3L;
        long financeOverviewSectionId = 4L;

        ProcessRole processRole = newProcessRole()
                .withOrganisationId(organisationId)
                .withRole(Role.LEADAPPLICANT)
                .build();

        List<Question> questions = newQuestion()
                .withMultipleStatuses(true)
                .build(1);

        List<QuestionStatusResource> questionStatusResources = newQuestionStatusResource()
                .withMarkedAsComplete(true)
                .withMarkedAsCompleteByOrganisationId(organisationId)
                .withQuestion(questions.get(0).getId())
                .build(1);

        Section financeSection = newSection()
                .withId(financeSectionId)
                .withSectionType(SectionType.FINANCE)
                .withQuestions(questions)
                .build();

        Section financeOverviewSection = newSection()
                .withId(financeOverviewSectionId)
                .withSectionType(SectionType.OVERVIEW_FINANCES)
                .withQuestions(questions)
                .build();

        Competition competition = newCompetition()
                .withSections(asList(financeSection, financeOverviewSection))
                .withQuestions(questions)
                .build();

        Application application = newApplication()
                .withId(applicationId)
                .withCompetition(competition)
                .withProcessRoles(processRole)
                .build();

        Map<Long, List<QuestionStatusResource>> completedQuestionStatuses = new HashMap<>();
        completedQuestionStatuses.put(organisationId, questionStatusResources);

        when(applicationRepositoryMock.findById(applicationId)).thenReturn(Optional.of(application));
        when(questionStatusServiceMock.findCompletedQuestionsByApplicationId(applicationId)).thenReturn(serviceSuccess(questionStatusResources));
        when(financeServiceMock.collaborativeFundingCriteriaMet(application.getId())).thenReturn(serviceSuccess(true));
        when(financeServiceMock.fundingSoughtValid(application.getId())).thenReturn(serviceSuccess(true));
        when(applicationValidatorService.isFinanceOverviewComplete(application)).thenReturn(true);

        Set<Long> result = sectionStatusService.getCompletedSections(applicationId, organisationId).getSuccess();

        assertTrue(result.contains(financeSectionId));
        assertTrue(result.contains(financeOverviewSectionId));
    }
}
