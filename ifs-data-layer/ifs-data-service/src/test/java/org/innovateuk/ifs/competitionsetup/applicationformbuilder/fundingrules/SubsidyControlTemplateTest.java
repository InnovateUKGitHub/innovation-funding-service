package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingrules;

import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
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

import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.junit.Assert.assertThat;
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

        when(questionnaireService.create(any())).thenAnswer((inv) -> {
            QuestionnaireResource questionnaire = inv.getArgument(0);
            questionnaire.setId(questionnaireId);
            return serviceSuccess(questionnaire);
        });
        when(questionnaireQuestionService.create(any())).thenAnswer((inv) -> serviceSuccess(inv.getArgument(0)));
        when(questionnaireOptionService.create(any())).thenAnswer((inv) -> serviceSuccess(inv.getArgument(0)));
        when(textOutcomeService.create(any())).thenAnswer((inv) -> serviceSuccess(inv.getArgument(0)));
        when(questionnaireRepository.findById(questionnaireId)).thenReturn(Optional.of(questionnaireEntity));

        subsidyControlTemplate.sections(newArrayList(
                projectDetails,
                SectionBuilder.aSection().withName("Finances")
        ));

        assertThat(projectDetails.getQuestions().get(0).getName(), is(equalTo("Subsidy basis")));
        assertThat(projectDetails.getQuestions().get(0).getQuestionnaire(), is(questionnaireEntity));

        verify(questionnaireService).create(any());
        verify(questionnaireQuestionService, times(2)).create(any());
        verify(questionnaireOptionService, times(4)).create(any());
        verify(textOutcomeService, times(3)).create(any());
    }
}