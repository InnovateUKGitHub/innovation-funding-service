package org.innovateuk.ifs.competitionsetup.service.sectionupdaters.application;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationDetailsForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationDetailsSectionSaverTest {

    @InjectMocks
    private ApplicationDetailsSectionSaver service;

    @Mock
    private CompetitionSetupRestService competitionSetupRestServiceMock;

    @Before
    public void setup() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        ReflectionTestUtils.setField(service, "validator", validator);
    }

    @Test
    public void testSectionToSave() {
        assertThat(service.subsectionToSave()).isEqualByComparingTo(CompetitionSetupSubsection.APPLICATION_DETAILS);
    }

    @Test
    public void testsSupportsForm() {
        assertThat(service.supportsForm(ApplicationDetailsForm.class)).isTrue();
        assertThat(service.supportsForm(CompetitionSetupForm.class)).isFalse();
    }

    @Test
    public void doSaveSection_validInputIsMappedProperlyAndResultsInUpdateRestCall() {
        CompetitionResource competitionResource = newCompetitionResource().withId(1L).build();
        ApplicationDetailsForm applicationDetailsForm = new ApplicationDetailsForm();
        applicationDetailsForm.setMinProjectDuration(9);
        applicationDetailsForm.setMaxProjectDuration(10);
        applicationDetailsForm.setUseResubmissionQuestion(true);

        when(competitionSetupRestServiceMock.update(any())).thenReturn(RestResult.restSuccess());

        ServiceResult<Void> result = service.doSaveSection(competitionResource, applicationDetailsForm);

        CompetitionResource expectedCompetitionResource = newCompetitionResource().build();
        expectedCompetitionResource.setId(1L);
        expectedCompetitionResource.setMinProjectDuration(9);
        expectedCompetitionResource.setMaxProjectDuration(10);
        expectedCompetitionResource.setUseResubmissionQuestion(true);

        assertThat(result.isSuccess()).isTrue();
        verify(competitionSetupRestServiceMock, times(1)).update(expectedCompetitionResource);
    }

    @Test
    public void doSaveSection_emptyFormReturnsEmptyFieldErrorsWhenFormIsEmpty() {
        CompetitionResource competitionResource = newCompetitionResource().build();
        ApplicationDetailsForm applicationDetailsForm = new ApplicationDetailsForm();

        ServiceResult<Void> result = service.doSaveSection(competitionResource, applicationDetailsForm);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getErrors().size()).isEqualTo(2);
        assertThat(result.getErrors())
                .filteredOn(error -> error.getFieldName().equals("minProjectDuration") &&
                        error.getErrorKey().equals("competition.setup.applicationdetails.min.projectduration"))
                .isNotEmpty();
        assertThat(result.getErrors())
                .filteredOn(error -> error.getFieldName().equals("maxProjectDuration") &&
                        error.getErrorKey().equals("competition.setup.applicationdetails.max.projectduration"))
                .isNotEmpty();

        verifyZeroInteractions(competitionSetupRestServiceMock);
    }

    @Test
    public void doSaveSection_emptyFormReturnsLargerThanErrorWhenMinFieldIsEqualToMaxField() {
        CompetitionResource competitionResource = newCompetitionResource().build();
        ApplicationDetailsForm applicationDetailsForm = new ApplicationDetailsForm();

        applicationDetailsForm.setMinProjectDuration(10);
        applicationDetailsForm.setMaxProjectDuration(10);

        ServiceResult<Void> result = service.doSaveSection(competitionResource, applicationDetailsForm);

        assertThat(result.isFailure());
        assertThat("minProjectDuration")
                .isEqualTo(result.getFailure().getErrors().get(0).getFieldName());
        assertThat("{competition.setup.applicationdetails.min.projectduration.larger}")
                .isEqualTo(result.getFailure().getErrors().get(0).getErrorKey());

        verifyZeroInteractions(competitionSetupRestServiceMock);
    }

    @Test
    public void doSaveSection_emptyFormReturnsLargerThanErrorWhenMinExceedsMaxField() {
        CompetitionResource competitionResource = newCompetitionResource().build();
        ApplicationDetailsForm applicationDetailsForm = new ApplicationDetailsForm();

        applicationDetailsForm.setMinProjectDuration(11);
        applicationDetailsForm.setMaxProjectDuration(10);

        ServiceResult<Void> result = service.doSaveSection(competitionResource, applicationDetailsForm);

        assertThat(result.isFailure()).isTrue();
        assertThat("minProjectDuration").isEqualTo(result.getFailure().getErrors().get(0).getFieldName());
        assertThat("{competition.setup.applicationdetails.min.projectduration.larger}")
                .isEqualTo(result.getFailure().getErrors().get(0).getErrorKey());

        verifyZeroInteractions(competitionSetupRestServiceMock);
    }
}
