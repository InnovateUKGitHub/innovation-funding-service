package org.innovateuk.ifs.competitionsetup.application.sectionupdater;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.application.form.DetailsForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationDetailsSectionSaverTest {

    @InjectMocks
    private DetailsSectionUpdater service;

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
        assertThat(service.supportsForm(DetailsForm.class)).isTrue();
        assertThat(service.supportsForm(CompetitionSetupForm.class)).isFalse();
    }

    @Test
    public void doSaveSection_validInputIsMappedProperlyAndResultsInUpdateRestCall() {
        CompetitionResource competitionResource = newCompetitionResource().withId(1L).build();
        DetailsForm detailsForm = new DetailsForm();
        detailsForm.setMinProjectDuration(new BigDecimal(9));
        detailsForm.setMaxProjectDuration(new BigDecimal(10));
        detailsForm.setUseResubmissionQuestion(true);

        when(competitionSetupRestServiceMock.update(any())).thenReturn(RestResult.restSuccess());

        ServiceResult<Void> result = service.doSaveSection(competitionResource, detailsForm);

        CompetitionResource expectedCompetitionResource = newCompetitionResource().build();
        expectedCompetitionResource.setId(1L);
        expectedCompetitionResource.setMinProjectDuration(9);
        expectedCompetitionResource.setMaxProjectDuration(10);
        expectedCompetitionResource.setUseResubmissionQuestion(true);

        assertThat(result.isSuccess()).isTrue();
        verify(competitionSetupRestServiceMock, times(1)).update(expectedCompetitionResource);
    }

    @Test
    public void doSaveSection_errorWhenFieldsAreEmpty() {
        CompetitionResource competitionResource = newCompetitionResource().build();
        DetailsForm detailsForm = new DetailsForm();

        ServiceResult<Void> result = service.doSaveSection(competitionResource, detailsForm);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getErrors().size()).isEqualTo(3);
        assertThat(result.getErrors())
                .filteredOn(error -> error.getFieldName().equals("minProjectDuration") &&
                        error.getErrorKey().equals("validation.field.must.not.be.blank"))
                .isNotEmpty();
        assertThat(result.getErrors())
                .filteredOn(error -> error.getFieldName().equals("maxProjectDuration") &&
                        error.getErrorKey().equals("validation.field.must.not.be.blank"))
                .isNotEmpty();
        assertThat(result.getErrors())
                .filteredOn(error -> error.getFieldName().equals("useResubmissionQuestion") &&
                        error.getErrorKey().equals("validation.application.must.indicate.resubmission.or.not"))
                .isNotEmpty();

        verifyZeroInteractions(competitionSetupRestServiceMock);
    }

    @Test
    public void doSaveSection_errorWhenProjectDurationsAreBelowMinimumAllowed() {
        CompetitionResource competitionResource = newCompetitionResource().build();
        DetailsForm detailsForm = new DetailsForm();
        detailsForm.setUseResubmissionQuestion(false);
        detailsForm.setMaxProjectDuration(new BigDecimal(0));
        detailsForm.setMinProjectDuration(new BigDecimal(0));

        ServiceResult<Void> result = service.doSaveSection(competitionResource, detailsForm);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getErrors().size()).isEqualTo(2);
        assertThat(result.getErrors())
                .filteredOn(error -> error.getFieldName().equals("minProjectDuration") &&
                        error.getErrorKey().equals("competition.setup.applicationdetails.projectduration.min"))
                .isNotEmpty();
        assertThat(result.getErrors())
                .filteredOn(error -> error.getFieldName().equals("maxProjectDuration") &&
                        error.getErrorKey().equals("competition.setup.applicationdetails.projectduration.min"))
                .isNotEmpty();

        verifyZeroInteractions(competitionSetupRestServiceMock);
    }

    @Test
    public void doSaveSection_errorWhenProjectDurationsAreNegative() {
        CompetitionResource competitionResource = newCompetitionResource().build();
        DetailsForm detailsForm = new DetailsForm();
        detailsForm.setUseResubmissionQuestion(false);
        detailsForm.setMaxProjectDuration(new BigDecimal(-1));
        detailsForm.setMinProjectDuration(new BigDecimal(-1));

        ServiceResult<Void> result = service.doSaveSection(competitionResource, detailsForm);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getErrors().size()).isEqualTo(2);
        assertThat(result.getErrors())
                .filteredOn(error -> error.getFieldName().equals("minProjectDuration") &&
                        error.getErrorKey().equals("competition.setup.applicationdetails.projectduration.min"))
                .isNotEmpty();
        assertThat(result.getErrors())
                .filteredOn(error -> error.getFieldName().equals("maxProjectDuration") &&
                        error.getErrorKey().equals("competition.setup.applicationdetails.projectduration.min"))
                .isNotEmpty();

        verifyZeroInteractions(competitionSetupRestServiceMock);
    }

    @Test
    public void doSaveSection_errorWhenDecimalsInProjectDurations() {
        CompetitionResource competitionResource = newCompetitionResource().build();
        DetailsForm detailsForm = new DetailsForm();
        detailsForm.setUseResubmissionQuestion(false);
        detailsForm.setMinProjectDuration(new BigDecimal(3.5));
        detailsForm.setMaxProjectDuration(new BigDecimal(3.5));

        ServiceResult<Void> result = service.doSaveSection(competitionResource, detailsForm);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getErrors().size()).isEqualTo(2);
        assertThat(result.getErrors())
                .filteredOn(error -> error.getFieldName().equals("minProjectDuration") &&
                        error.getErrorKey().equals("validation.standard.integer.non.decimal.format"))
                .isNotEmpty();
        assertThat(result.getErrors())
                .filteredOn(error -> error.getFieldName().equals("maxProjectDuration") &&
                        error.getErrorKey().equals("validation.standard.integer.non.decimal.format"))
                .isNotEmpty();

        verifyZeroInteractions(competitionSetupRestServiceMock);
    }

    @Test
    public void doSaveSection_successWhenMinFieldIsEqualToMaxField() {
        CompetitionResource competitionResource = newCompetitionResource().build();
        DetailsForm detailsForm = new DetailsForm();

        detailsForm.setMinProjectDuration(new BigDecimal(10));
        detailsForm.setMaxProjectDuration(new BigDecimal(10));
        detailsForm.setUseResubmissionQuestion(true);

        when(competitionSetupRestServiceMock.update(any())).thenReturn(RestResult.restSuccess());

        ServiceResult<Void> result = service.doSaveSection(competitionResource, detailsForm);

        assertThat(result.isSuccess()).isTrue();

        verify(competitionSetupRestServiceMock, times(1)).update(any());
    }

    @Test
    public void doSaveSection_errorWhenMinFieldExceedsMaxField() {
        CompetitionResource competitionResource = newCompetitionResource().build();
        DetailsForm detailsForm = new DetailsForm();

        detailsForm.setMinProjectDuration(new BigDecimal(11));
        detailsForm.setMaxProjectDuration(new BigDecimal(10));
        detailsForm.setUseResubmissionQuestion(false);

        ServiceResult<Void> result = service.doSaveSection(competitionResource, detailsForm);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getErrors().size()).isEqualTo(2);
        assertThat(result.getErrors())
                .filteredOn(error -> error.getFieldName().equals("minProjectDuration") &&
                        error.getErrorKey().equals("competition.setup.applicationdetails.min.projectduration.exceedsmax"))
                .isNotEmpty();
        assertThat(result.getErrors())
                .filteredOn(error -> error.getFieldName().equals("maxProjectDuration") &&
                        error.getErrorKey().equals("competition.setup.applicationdetails.max.projectduration.beneathmin"))
                .isNotEmpty();

        verifyZeroInteractions(competitionSetupRestServiceMock);
    }

    @Test
    public void doSaveSection_errorWhenProjectDurationsExceedMaximumAllowed() {
        CompetitionResource competitionResource = newCompetitionResource().build();
        DetailsForm detailsForm = new DetailsForm();
        detailsForm.setMinProjectDuration(new BigDecimal(61));
        detailsForm.setMaxProjectDuration(new BigDecimal(61));
        detailsForm.setUseResubmissionQuestion(false);

        ServiceResult<Void> result = service.doSaveSection(competitionResource, detailsForm);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getErrors().size()).isEqualTo(2);
        assertThat(result.getErrors())
                .filteredOn(error -> error.getFieldName().equals("minProjectDuration") &&
                        error.getErrorKey().equals("competition.setup.applicationdetails.projectduration.max"))
                .isNotEmpty();
        assertThat(result.getErrors())
                .filteredOn(error -> error.getFieldName().equals("maxProjectDuration") &&
                        error.getErrorKey().equals("competition.setup.applicationdetails.projectduration.max"))
                .isNotEmpty();

        verifyZeroInteractions(competitionSetupRestServiceMock);
    }
}
