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
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Stream.concat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.form.resource.SectionType.FINANCE;
import static org.innovateuk.ifs.form.resource.SectionType.GENERAL;
import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public class ApplicationOverviewSectionModelPopulatorTest extends BaseServiceUnitTest<ApplicationOverviewSectionModelPopulator> {

    @Mock
    private SectionService sectionService;

    @Mock
    private ApplicantRestService applicantRestService;

    @Mock
    private AssignButtonsPopulator assignButtonsPopulator;

    @Override
    protected ApplicationOverviewSectionModelPopulator supplyServiceUnderTest() {
        return new ApplicationOverviewSectionModelPopulator(sectionService, applicantRestService,
                assignButtonsPopulator);
    }

    @Test
    public void populate() {
        CompetitionResource competition = newCompetitionResource().build();
        ApplicationResource application = newApplicationResource().build();
        long userId = 1L;

        List<QuestionResource> questionsSection1 = newQuestionResource().build(2);
        List<QuestionResource> questionsSection2 = newQuestionResource().build(2);

        List<ApplicantQuestionResource> applicantQuestionsSection1 = newApplicantQuestionResource()
                .withQuestion(questionsSection1.get(0), questionsSection1.get(1))
                .withApplicantQuestionStatuses(emptyList())
                .build(2);

        List<ApplicantQuestionResource> applicantQuestionsSection2 = newApplicantQuestionResource()
                .withQuestion(questionsSection2.get(0), questionsSection2.get(1))
                .withApplicantQuestionStatuses(emptyList())
                .build(2);

        List<SectionResource> sections = newSectionResource().build(6);

        List<SectionResource> childSectionsParent1 = asList(sections.get(2), sections.get(3));
        List<SectionResource> childSectionsParent2 = asList(sections.get(4), sections.get(5));

        List<SectionResource> parentSections = newSectionResource()
                .withId(sections.get(0).getId(), sections.get(1).getId())
                .withPriority(1, 2)
                .withChildSections(
                        simpleMap(childSectionsParent1, SectionResource::getId),
                        simpleMap(childSectionsParent2, SectionResource::getId))
                .withType(GENERAL, FINANCE)
                .build(2);
        List<ApplicantSectionResource> applicantSectionResources = newApplicantSectionResource()
                .withSection(parentSections.get(0), parentSections.get(1))
                .withApplicantQuestions(applicantQuestionsSection1, applicantQuestionsSection2)
                .build(2);

        when(sectionService.getAllByCompetitionId(competition.getId())).thenReturn(sections);
        when(sectionService.filterParentSections(sections)).thenReturn(parentSections);

        forEachWithIndex(parentSections, (index, sectionResource) ->
                when(applicantRestService.getSection(userId, application.getId(), sectionResource.getId()))
                        .thenReturn(applicantSectionResources.get(index)));

        applicantSectionResources.forEach(applicantSectionResource -> when(assignButtonsPopulator.populate(
                eq(applicantSectionResource), isA(ApplicantQuestionResource.class), eq(false)))
                .thenReturn(new AssignButtonsViewModel()));

        ApplicationOverviewSectionViewModel result = service.populate(competition, application, userId);

        Map<Long, List<SectionResource>> expectedSubSections = asMap(parentSections.get(0).getId(),
                childSectionsParent1,
                parentSections.get(1).getId(),
                childSectionsParent2);

        Map<Long, List<QuestionResource>> expectedSectionQuestions = asMap(
                parentSections.get(0).getId(), questionsSection1, parentSections.get(1).getId(), questionsSection2);

        Long[] questionIds =
                concat(questionsSection1.stream(), questionsSection2.stream()).map(QuestionResource::getId).toArray(Long[]::new);

        assertEquals(parentSections, result.getSections());
        assertEquals(expectedSubSections, result.getSubSections());
        assertEquals(expectedSectionQuestions, result.getSectionQuestions());
        assertThat(result.getAssignButtonViewModels()).containsOnlyKeys(questionIds);
        assertTrue(result.isHasFinanceSection());
        assertEquals(parentSections.get(1).getId(), result.getFinanceSectionId());
    }
}