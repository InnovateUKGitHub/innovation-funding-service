package org.innovateuk.ifs.application.overview.populator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewSectionViewModel;
import org.innovateuk.ifs.application.populator.AssignButtonsPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.AssignButtonsViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Stream.concat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CollaborationLevel.COLLABORATIVE;
import static org.innovateuk.ifs.competition.resource.CollaborationLevel.SINGLE;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.form.resource.SectionType.FINANCE;
import static org.innovateuk.ifs.form.resource.SectionType.GENERAL;
import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

public class ApplicationOverviewSectionModelPopulatorTest extends BaseServiceUnitTest<ApplicationOverviewSectionModelPopulator> {

    @Mock
    private SectionService sectionService;

    @Mock
    private ApplicantRestService applicantRestService;

    @Mock
    private AssignButtonsPopulator assignButtonsPopulator;

    @Mock
    private MessageSource messageSource;

    private long userId = 1L;
    private CompetitionResource competition;
    private ApplicationResource application;
    private List<SectionResource> parentSections;
    private List<QuestionResource> questionsSection1;
    private List<QuestionResource> questionsSection2;
    private List<QuestionResource> questionsSection3;
    private List<SectionResource> childSectionsParent1;
    private List<SectionResource> childSectionsParent2;
    private List<SectionResource> childSectionsParent3;
    private List<ApplicantQuestionResource> applicantQuestionsSection1;
    private List<ApplicantQuestionResource> applicantQuestionsSection2;
    private List<ApplicantQuestionResource> applicantQuestionsSection3;
    private List<SectionResource> allSections;

    @Override
    protected ApplicationOverviewSectionModelPopulator supplyServiceUnderTest() {
        return new ApplicationOverviewSectionModelPopulator(sectionService, applicantRestService,
                assignButtonsPopulator, messageSource);
    }

    @Before
    public void setUp() {
        competition = newCompetitionResource()
                .withCollaborationLevel(SINGLE)
                .build();
        application = newApplicationResource().build();

        questionsSection1 = newQuestionResource().build(2);
        questionsSection2 = newQuestionResource().build(2);
        questionsSection3 = newQuestionResource().build(2);

        applicantQuestionsSection1 = newApplicantQuestionResource()
                .withQuestion(questionsSection1.get(0), questionsSection1.get(1))
                .withApplicantQuestionStatuses(emptyList())
                .build(2);

        applicantQuestionsSection2 = newApplicantQuestionResource()
                .withQuestion(questionsSection2.get(0), questionsSection2.get(1))
                .withApplicantQuestionStatuses(emptyList())
                .build(2);

        applicantQuestionsSection3 = newApplicantQuestionResource()
                .withQuestion(questionsSection3.get(0), questionsSection3.get(1))
                .withApplicantQuestionStatuses(emptyList())
                .build(2);

        allSections = newSectionResource().build(9);

        childSectionsParent1 = asList(allSections.get(3), allSections.get(4));
        childSectionsParent2 = asList(allSections.get(5), allSections.get(6));
        childSectionsParent3 = asList(allSections.get(7), allSections.get(8));

        parentSections = newSectionResource()
                .withId(allSections.get(0).getId(), allSections.get(1).getId(), allSections.get(2).getId())
                .withName("Application questions", "Finances", "Your finances")
                .withPriority(1, 2, 3)
                .withChildSections(
                        simpleMap(childSectionsParent1, SectionResource::getId),
                        simpleMap(childSectionsParent2, SectionResource::getId),
                        simpleMap(childSectionsParent3, SectionResource::getId))
                .withType(GENERAL, GENERAL, FINANCE)
                .build(3);
        List<ApplicantSectionResource> applicantSectionResources = newApplicantSectionResource()
                .withSection(parentSections.get(0), parentSections.get(1), parentSections.get(2))
                .withApplicantQuestions(applicantQuestionsSection1, applicantQuestionsSection2,
                        applicantQuestionsSection3)
                .build(3);

        when(sectionService.getAllByCompetitionId(competition.getId())).thenReturn(allSections);
        when(sectionService.filterParentSections(allSections)).thenReturn(parentSections);

        forEachWithIndex(parentSections, (index, sectionResource) ->
                when(applicantRestService.getSection(userId, application.getId(), sectionResource.getId()))
                        .thenReturn(applicantSectionResources.get(index)));

        applicantSectionResources.forEach(applicantSectionResource -> when(assignButtonsPopulator.populate(
                eq(applicantSectionResource), isA(ApplicantQuestionResource.class), eq(false)))
                .thenReturn(new AssignButtonsViewModel()));
    }

    @Test
    public void populate() {
        when(messageSource.getMessage("ifs.section.finances.description", null, Locale.ENGLISH))
                .thenReturn("Finances description");

        ApplicationOverviewSectionViewModel result = service.populate(competition, application, userId);

        Map<Long, List<SectionResource>> expectedSubSections = asMap(parentSections.get(0).getId(),
                childSectionsParent1,
                parentSections.get(1).getId(),
                childSectionsParent2,
                parentSections.get(2).getId(),
                childSectionsParent3);

        Map<Long, List<QuestionResource>> expectedSectionQuestions = asMap(
                parentSections.get(0).getId(),
                questionsSection1,
                parentSections.get(1).getId(),
                questionsSection2,
                parentSections.get(2).getId(),
                questionsSection3);

        Long[] questionIds =
                concat(concat(questionsSection1.stream(), questionsSection2.stream()), questionsSection3.stream())
                        .map(QuestionResource::getId).toArray(Long[]::new);

        assertEquals(parentSections, result.getSections());
        assertEquals(expectedSubSections, result.getSubSections());
        assertEquals(expectedSectionQuestions, result.getSectionQuestions());
        assertThat(result.getAssignButtonViewModels()).containsOnlyKeys(questionIds);
        assertTrue(result.isHasFinanceSection());
        assertEquals(parentSections.get(2).getId(), result.getFinanceSectionId());
        assertEquals("Finances description", result.getSections().get(1).getDescription());

        verify(sectionService).getAllByCompetitionId(competition.getId());
        verify(sectionService).filterParentSections(allSections);
        verify(applicantRestService, times(parentSections.size())).getSection(eq(userId), eq(application.getId()),
                isA(Long.class));
        verify(assignButtonsPopulator,
                times(applicantQuestionsSection1.size() + applicantQuestionsSection2.size() + applicantQuestionsSection3.size()))
                .populate(isA(ApplicantSectionResource.class), isA(ApplicantQuestionResource.class), eq(false));
        verify(messageSource, only()).getMessage("ifs.section.finances.description", null, Locale.ENGLISH);
    }

    @Test
    public void populate_collaborativeCompetition() {
        competition.setCollaborationLevel(COLLABORATIVE);

        when(messageSource.getMessage("ifs.section.finances.collaborative.description", null, Locale.ENGLISH))
                .thenReturn("Finances collaborative description");

        ApplicationOverviewSectionViewModel result = service.populate(competition, application, userId);

        assertEquals("Finances collaborative description", result.getSections().get(1).getDescription());
        verify(messageSource, only()).getMessage("ifs.section.finances.collaborative.description", null,
                Locale.ENGLISH);
    }
}