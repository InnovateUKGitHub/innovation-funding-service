package org.innovateuk.ifs.eugrant.scheduled;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.euactiontype.domain.EuActionType;
import org.innovateuk.ifs.euactiontype.mapper.EuActionTypeMapper;
import org.innovateuk.ifs.euactiontype.repository.EuActionTypeRepository;
import org.innovateuk.ifs.eugrant.EuActionTypeResource;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.eugrant.builder.EuActionTypeResourceBuilder.newEuActionTypeResource;
import static org.innovateuk.ifs.eugrant.scheduled.CsvHeader.*;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TODO DW - document this class
 */
@RunWith(MockitoJUnitRunner.class)
public class GrantResourceBuilderTest {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @InjectMocks
    private GrantResourceBuilder builder;

    @Mock
    private EuActionTypeRepository euActionTypeRepositoryMock;

    @Mock
    private EuActionTypeMapper euActionTypeMapperMock;

    @Test
    public void convertDataRowsToEuGrantResources() {

        List<Map<CsvHeader, String>> data = asList(

            asMap(ORGANISATION_TYPE, "Research", ORGANISATION_NAME, "The University of Sheffield",
                    COMPANIES_HOUSE_REGISTRATION_NUMBER, "", CONTACT_FULL_NAME, "Bob Bobbins",
                    CONTACT_JOB_TITLE, "Project manager", CONTACT_EMAIL_ADDRESS, "bob.bobbins@example.com",
                    CONTACT_TELEPHONE_NUMBER, "01234 567890", GRANT_AGREEMENT_NUMBER, "111222",
                    PIC, "998592400", ACTION_TYPE, "(CSA) Coordination and support action",
                    PROJECT_NAME, "An interesting project 1", PROJECT_START_DATE, "01/12/2018",
                    PROJECT_END_DATE, "28/02/2021", PROJECT_EU_FUNDING_CONTRIBUTION, "132470.07",
                    PROJECT_COORDINATOR, "BENEFICIARY"),

            asMap(ORGANISATION_TYPE, "Business", ORGANISATION_NAME, "Empire Ltd",
                    COMPANIES_HOUSE_REGISTRATION_NUMBER, "123456789", CONTACT_FULL_NAME, "Steve Smith",
                    CONTACT_JOB_TITLE, "Project administrator", CONTACT_EMAIL_ADDRESS, "steve.smith@example.com",
                    CONTACT_TELEPHONE_NUMBER, "09876 543210", GRANT_AGREEMENT_NUMBER, "333444",
                    PIC, "999763772", ACTION_TYPE, "(SME-1) SME Instrument phase 1",
                    PROJECT_NAME, "An interesting project 2", PROJECT_START_DATE, "30/01/2018",
                    PROJECT_END_DATE, "01/04/2022", PROJECT_EU_FUNDING_CONTRIBUTION, "437766.84",
                    PROJECT_COORDINATOR, "COORDINATOR")
        );

        EuActionType csaActionType = new EuActionType();
        EuActionType smeActionType = new EuActionType();
        EuActionTypeResource csaActionTypeResource = newEuActionTypeResource().withName("CSA").build();
        EuActionTypeResource smeActionTypeResource = newEuActionTypeResource().withName("SME-1").build();

        when(euActionTypeRepositoryMock.findOneByName("CSA")).thenReturn(Optional.of(csaActionType));
        when(euActionTypeRepositoryMock.findOneByName("SME-1")).thenReturn(Optional.of(smeActionType));
        when(euActionTypeMapperMock.mapToResource(csaActionType)).thenReturn(csaActionTypeResource);
        when(euActionTypeMapperMock.mapToResource(smeActionType)).thenReturn(smeActionTypeResource);

        ServiceResult<List<ServiceResult<EuGrantResource>>> results = builder.convertDataRowsToEuGrantResources(data);

        assertThat(results.isSuccess()).isTrue();

        verify(euActionTypeRepositoryMock).findOneByName("CSA");
        verify(euActionTypeRepositoryMock).findOneByName("SME-1");
        verify(euActionTypeMapperMock).mapToResource(csaActionType);
        verify(euActionTypeMapperMock).mapToResource(smeActionType);

        List<ServiceResult<EuGrantResource>> resourceResults = results.getSuccess();
        assertThat(resourceResults).hasSize(2);

        EuGrantResource grant1 = resourceResults.get(0).getSuccess();
        Map<CsvHeader, String> originalRow1 = data.get(0);

        EuGrantResource grant2 = resourceResults.get(1).getSuccess();
        Map<CsvHeader, String> originalRow2 = data.get(1);

        assertThatEuGrantResourceMatchesOriginalData(grant1, originalRow1, csaActionTypeResource, false);
        assertThatEuGrantResourceMatchesOriginalData(grant2, originalRow2, smeActionTypeResource, true);
    }

    private void assertThatEuGrantResourceMatchesOriginalData(EuGrantResource grant, Map<CsvHeader, String> originalRow,
                                                              EuActionTypeResource expectedActionType,
                                                              boolean expectedProjectCoordinator) {

        assertThat(grant.getId().toString()).isNotEmpty();

        // TODO DW - reinstate assertions below
//        assertThat(grant.getShortCode()).isNotEmpty();
//        assertThat(grant.isContactComplete()).isNotEmpty();
//        assertThat(grant.isFundingComplete()).isNotEmpty();
//        assertThat(grant.isOrganisationComplete()).isNotEmpty();

        assertThat(grant.getContact().getEmail()).isEqualTo(originalRow.get(CONTACT_EMAIL_ADDRESS));
        assertThat(grant.getContact().getJobTitle()).isEqualTo(originalRow.get(CONTACT_JOB_TITLE));
        assertThat(grant.getContact().getName()).isEqualTo(originalRow.get(CONTACT_FULL_NAME));
        assertThat(grant.getContact().getTelephone()).isEqualTo(originalRow.get(CONTACT_TELEPHONE_NUMBER));

        assertThat(grant.getFunding().getActionType()).isEqualTo(expectedActionType);
        assertThat(grant.getFunding().getFundingContribution()).isEqualTo(originalRow.get(PROJECT_EU_FUNDING_CONTRIBUTION));
        assertThat(grant.getFunding().getGrantAgreementNumber()).isEqualTo(originalRow.get(GRANT_AGREEMENT_NUMBER));
        assertThat(grant.getFunding().getParticipantId()).isEqualTo(originalRow.get(PIC));
        assertThat(grant.getFunding().getProjectStartDate()).isEqualTo(LocalDate.from(DATE_FORMAT.parse(originalRow.get(PROJECT_START_DATE))));
        assertThat(grant.getFunding().getProjectEndDate()).isEqualTo(LocalDate.from(DATE_FORMAT.parse(originalRow.get(PROJECT_END_DATE))));
        assertThat(grant.getFunding().getProjectName()).isEqualTo(originalRow.get(PROJECT_NAME));
        assertThat(grant.getFunding().isProjectCoordinator()).isEqualTo(expectedProjectCoordinator);

        assertThat(grant.getOrganisation().getCompaniesHouseNumber()).isEqualTo(originalRow.get(COMPANIES_HOUSE_REGISTRATION_NUMBER));
        assertThat(grant.getOrganisation().getName()).isEqualTo(originalRow.get(ORGANISATION_NAME));
        assertThat(grant.getOrganisation().getOrganisationType().getDisplayName()).isEqualTo(originalRow.get(ORGANISATION_TYPE));
    }
}
