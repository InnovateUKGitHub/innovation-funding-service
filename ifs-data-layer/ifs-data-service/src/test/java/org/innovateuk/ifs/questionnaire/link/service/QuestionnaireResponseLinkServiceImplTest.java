package org.innovateuk.ifs.questionnaire.link.service;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.questionnaire.config.domain.Questionnaire;
import org.innovateuk.ifs.questionnaire.config.repository.QuestionnaireRepository;
import org.innovateuk.ifs.questionnaire.link.domain.ApplicationOrganisationQuestionnaireResponse;
import org.innovateuk.ifs.questionnaire.link.repository.ApplicationOrganisationQuestionnaireResponseRepository;
import org.innovateuk.ifs.questionnaire.response.domain.QuestionnaireResponse;
import org.innovateuk.ifs.questionnaire.response.repository.QuestionnaireResponseRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireResponseLinkServiceImplTest {

    @InjectMocks
    private QuestionnaireResponseLinkServiceImpl service;

    @Mock
    private ApplicationOrganisationQuestionnaireResponseRepository applicationOrganisationQuestionnaireResponseRepository;

    @Mock
    private QuestionnaireRepository questionnaireRepository;

    @Mock
    private QuestionnaireResponseRepository questionnaireResponseRepository;

    @Mock
    protected OrganisationRepository organisationRepository;

    @Mock
    protected ApplicationRepository applicationRepository;

    @Test
    public void getResponseIdByApplicationIdAndOrganisationIdAndQuestionnaireId() {
        long applicationId = 1L;
        long organisationId = 2L;
        long questionnaireId = 3L;
        UUID responseId = UUID.randomUUID();

        ApplicationOrganisationQuestionnaireResponse link = new ApplicationOrganisationQuestionnaireResponse();
        QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse();
        questionnaireResponse.setId(responseId);
        link.setQuestionnaireResponse(questionnaireResponse);

        when(applicationOrganisationQuestionnaireResponseRepository.existsByApplicationIdAndOrganisationIdAndQuestionnaireResponseQuestionnaireId(applicationId, organisationId, questionnaireId)).thenReturn(true);
        when(applicationOrganisationQuestionnaireResponseRepository.findByApplicationIdAndOrganisationIdAndQuestionnaireResponseQuestionnaireId(applicationId, organisationId, questionnaireId)).thenReturn(Optional.of(link));

        assertThat(service.getResponseIdByApplicationIdAndOrganisationIdAndQuestionnaireId(applicationId, organisationId, questionnaireId).getSuccess(),
                is(responseId));
    }

    @Test
    public void getResponseIdByApplicationIdAndOrganisationIdAndQuestionnaireId_create() {
        long applicationId = 1L;
        long organisationId = 2L;
        long questionnaireId = 3L;
        UUID responseId = UUID.randomUUID();

        Application application = newApplication().build();
        Organisation organisation = newOrganisation().build();
        Questionnaire questionnaire = new Questionnaire();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(organisationRepository.findById(organisationId)).thenReturn(Optional.of(organisation));
        when(questionnaireRepository.findById(questionnaireId)).thenReturn(Optional.of(questionnaire));

        when(questionnaireResponseRepository.save(any())).thenAnswer(inv -> {
            QuestionnaireResponse response = inv.getArgument(0);
            response.setId(responseId);
            return response;
        });
        when(applicationOrganisationQuestionnaireResponseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(applicationOrganisationQuestionnaireResponseRepository.existsByApplicationIdAndOrganisationIdAndQuestionnaireResponseQuestionnaireId(applicationId, organisationId, questionnaireId)).thenReturn(false);

        assertThat(service.getResponseIdByApplicationIdAndOrganisationIdAndQuestionnaireId(applicationId, organisationId, questionnaireId).getSuccess(),
                is(responseId));

        verify(applicationOrganisationQuestionnaireResponseRepository).save(argThat(lambdaMatches(aoqr -> {
            assertThat(aoqr.getApplication(), is(application));
            assertThat(aoqr.getOrganisation(), is(organisation));
            assertThat(aoqr.getQuestionnaireResponse().getQuestionnaire(), is(questionnaire));
            return true;
        })));
    }
}