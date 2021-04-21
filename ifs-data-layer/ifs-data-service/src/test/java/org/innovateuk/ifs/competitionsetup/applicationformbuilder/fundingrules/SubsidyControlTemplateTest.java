package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingrules;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.questionnaire.config.domain.Questionnaire;
import org.innovateuk.ifs.questionnaire.config.repository.QuestionnaireRepository;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireOptionService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireQuestionService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireTextOutcomeService;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder.aQuestion;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder.aSection;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SubsidyControlTemplateTest {

    @InjectMocks
    private SubsidyControlTemplate subsidyControlTemplate;

    @Mock
    private QuestionnaireService questionnaireService;

    @Mock
    private QuestionnaireQuestionService questionnaireQuestionService;

    @Mock
    private QuestionnaireOptionService questionnaireOptionService;

    @Mock
    private QuestionnaireTextOutcomeService textOutcomeService;

    @Mock
    private QuestionnaireRepository questionnaireRepository;

    @Mock
    private Environment environment;

    @Test
    public void sections() {
        ReflectionTestUtils.setField(subsidyControlTemplate, "northernIrelandSubsidyControlToggle", true);
        long questionnaireId = 1L;
        Questionnaire questionnaireEntity = new Questionnaire();
        SectionBuilder projectDetails = aSection().withName("Project details").withType(SectionType.PROJECT_DETAILS);

        when(environment.getActiveProfiles()).thenReturn(new String[0]);
        when(questionnaireService.create(any())).thenAnswer((inv) -> {
            QuestionnaireResource questionnaire = inv.getArgument(0);
            questionnaire.setId(questionnaireId);
            return serviceSuccess(questionnaire);
        });
        when(questionnaireQuestionService.create(any())).thenAnswer((inv) -> serviceSuccess(inv.getArgument(0)));
        when(questionnaireOptionService.create(any())).thenAnswer((inv) -> serviceSuccess(inv.getArgument(0)));
        when(textOutcomeService.create(any())).thenAnswer((inv) -> serviceSuccess(inv.getArgument(0)));
        when(questionnaireRepository.findById(questionnaireId)).thenReturn(Optional.of(questionnaireEntity));
        Competition competition = newCompetition().build();

        subsidyControlTemplate.sections(competition, newArrayList(
                projectDetails,
                aSection().withType(SectionType.FINANCES)
        ));

        assertThat(projectDetails.getQuestions().get(0).getName()).isEqualTo("Subsidy basis");
        assertThat(projectDetails.getQuestions().get(0).getQuestionnaire()).isEqualTo(questionnaireEntity);

        verify(questionnaireService).create(any());
        verify(questionnaireQuestionService, times(2)).create(any());
        verify(questionnaireOptionService, times(4)).create(any());
        verify(textOutcomeService, times(3)).create(any());
    }

    @Test
    public void shouldInjectQuestionToProjectDetails() {
        ReflectionTestUtils.setField(subsidyControlTemplate, "northernIrelandSubsidyControlToggle", false);
        SectionBuilder projectDetails = aSection().withType(SectionType.PROJECT_DETAILS)
                .withQuestions(newArrayList(aQuestion().withName("question1")));
        Competition competition = newCompetition().build();

        subsidyControlTemplate.sections(competition, newArrayList(
                projectDetails,
                aSection().withType(SectionType.FINANCES)
        ));

        assertThat(projectDetails.getQuestions()).hasSize(2);
        assertThat(projectDetails.getQuestions().get(0).getQuestionSetupType()).isEqualTo(QuestionSetupType.NORTHERN_IRELAND_DECLARATION);
    }

    @Test
    public void shouldNotInjectQuestionToProjectDetailsIfNoFinancesSectionPresent() {
        ReflectionTestUtils.setField(subsidyControlTemplate, "northernIrelandSubsidyControlToggle", false);
        SectionBuilder projectDetails = aSection().withType(SectionType.PROJECT_DETAILS)
                .withQuestions(newArrayList(aQuestion().withName("question1")));
        Competition competition = newCompetition().build();

         subsidyControlTemplate.sections(competition, newArrayList(
                projectDetails
        ));

        assertThat(projectDetails.getQuestions()).hasSize(1);
        assertThat(projectDetails.getQuestions().get(0).getName()).isEqualTo("question1");
    }

}