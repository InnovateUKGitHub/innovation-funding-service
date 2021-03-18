package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingrules;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
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

    @Test
    public void sections() {
        long questionnaireId = 1L;
        Questionnaire questionnaireEntity = new Questionnaire();
        SectionBuilder projectDetails = SectionBuilder.aSection().withName("Project details");

        Competition competition = newCompetition().build();

        when(questionnaireService.create(any())).thenAnswer((inv) -> {
            QuestionnaireResource questionnaire = inv.getArgument(0);
            questionnaire.setId(questionnaireId);
            return serviceSuccess(questionnaire);
        });
        when(questionnaireQuestionService.create(any())).thenAnswer((inv) -> serviceSuccess(inv.getArgument(0)));
        when(questionnaireOptionService.create(any())).thenAnswer((inv) -> serviceSuccess(inv.getArgument(0)));
        when(textOutcomeService.create(any())).thenAnswer((inv) -> serviceSuccess(inv.getArgument(0)));
        when(questionnaireRepository.findById(questionnaireId)).thenReturn(Optional.of(questionnaireEntity));

        subsidyControlTemplate.sections(competition, newArrayList(
                projectDetails,
                SectionBuilder.aSection().withName("Finances")
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
        SectionBuilder projectDetails = SectionBuilder.aSection().withName("Project details");

        Competition competition = newCompetition().build();

        subsidyControlTemplate.sections(competition, newArrayList(
                projectDetails,
                SectionBuilder.aSection().withName("Finances")
        ));

        assertThat(projectDetails.getQuestions()).hasSize(2);
        assertThat(projectDetails.getQuestions().get(0).getQuestionSetupType()).isEqualTo(QuestionSetupType.NORTHERN_IRELAND_DECLARATION);
    }

    @Test
    public void shouldNotInjectQuestionToProjectDetailsIfPrincesTrustComp() {
        ReflectionTestUtils.setField(subsidyControlTemplate, "northernIrelandSubsidyControlToggle", false);
        SectionBuilder projectDetails = SectionBuilder.aSection().withName("Project details");

        Competition competition = newCompetition().withCompetitionType(
                newCompetitionType()
                        .withName(CompetitionTypeEnum.THE_PRINCES_TRUST.getText())
                        .build())
                .build();

         subsidyControlTemplate.sections(competition, newArrayList(
                projectDetails,
                SectionBuilder.aSection().withName("Finances")
        ));

        assertThat(projectDetails.getQuestions()).hasSize(1);
        assertThat(projectDetails.getQuestions().get(0).getName()).isEqualTo("question1");
    }

    @Test
    public void shouldNotInjectQuestionToProjectDetailsIfExpressionOfInterestComp() {
        ReflectionTestUtils.setField(subsidyControlTemplate, "northernIrelandSubsidyControlToggle", false);
        SectionBuilder projectDetails = SectionBuilder.aSection().withName("Project details");

        Competition competition = newCompetition().withCompetitionType(
                newCompetitionType()
                        .withName(CompetitionTypeEnum.EXPRESSION_OF_INTEREST.getText())
                        .build())
                .build();

        subsidyControlTemplate.sections(competition, newArrayList(
                projectDetails,
                SectionBuilder.aSection().withName("Finances")
        ));

        assertThat(projectDetails.getQuestions()).hasSize(1);
        assertThat(projectDetails.getQuestions().get(0).getName()).isEqualTo("question1");
    }
}