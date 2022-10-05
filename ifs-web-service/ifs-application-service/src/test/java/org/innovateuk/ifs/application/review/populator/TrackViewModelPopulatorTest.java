package org.innovateuk.ifs.application.review.populator;

import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.review.viewmodel.TrackViewModel;
import org.innovateuk.ifs.application.service.ApplicationEoiEvidenceResponseRestService;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionEoiDocumentResource;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionEoiEvidenceConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.resource.FileTypeResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.file.service.FileTypeRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceState.NOT_SUBMITTED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionTypeEnum.GENERIC;
import static org.innovateuk.ifs.competition.resource.CompetitionTypeEnum.HORIZON_EUROPE_GUARANTEE;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.file.builder.FileTypeResourceBuilder.newFileTypeResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TrackViewModelPopulatorTest {

    @InjectMocks
    private TrackViewModelPopulator populator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private ApplicationEoiEvidenceResponseRestService applicationEoiEvidenceResponseRestService;

    @Mock
    private CompetitionEoiEvidenceConfigRestService competitionEoiEvidenceConfigRestService;

    @Mock
    private FileEntryRestService fileEntryRestService;

    @Mock
    private FileTypeRestService fileTypeRestService;

    @Mock
    private ProcessRoleRestService processRoleRestService;

    private static final String EARLY_METRICS_URL = "www.early-metrics.com";

    @Test
    public void populateForHecpEoiEvidenceRequired() {

        long applicationId = 1L;
        long organisationId = 2L;
        long fileEntryId = 3L;
        long competitionId = 100L;
        long eoiEvidenceConfigResourceId = 1L;

        UserResource user = newUserResource().withRoleGlobal(Role.APPLICANT).build();
        CompetitionEoiEvidenceConfigResource eoiEvidenceConfigResource = CompetitionEoiEvidenceConfigResource.builder()
                .evidenceRequired(true)
                .evidenceTitle("Eoi title")
                .evidenceGuidance("eoi guidance")
                .competitionId(competitionId)
                .id(eoiEvidenceConfigResourceId)
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withName("Generic competition")
                .withCompetitionEoiEvidenceConfigResource(eoiEvidenceConfigResource)
                .withEnabledForExpressionOfInterest(true)
                .withCompetitionTypeEnum(HORIZON_EUROPE_GUARANTEE)
                .build();
        OrganisationResource organisation = newOrganisationResource().withId(organisationId).build();
        ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource = ApplicationEoiEvidenceResponseResource.builder()
                .applicationId(applicationId)
                .organisationId(organisationId)
                .fileEntryId(fileEntryId)
                .id(3L)
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withCompetition(competition.getId())
                .withLeadOrganisationId(organisation.getId())
                .withApplicationEoiEvidenceResponseResource(applicationEoiEvidenceResponseResource)
                .build();
        FileEntryResource fileEntryResource = newFileEntryResource().withId(fileEntryId).withName("Eoi evidence file").build();
        CompetitionEoiDocumentResource competitionEoiDocumentResource = CompetitionEoiDocumentResource.builder()
                .competitionEoiEvidenceConfigId(eoiEvidenceConfigResource.getId())
                .fileTypeId(1L)
                .build();
        FileTypeResource fileTypeResource = newFileTypeResource().withId(1L).withName("PDF").withExtension(".pdf").build();
        ProcessRoleResource processRoleResource = newProcessRoleResource()
                .withApplication(applicationId)
                .withRole(ProcessRoleType.LEADAPPLICANT)
                .withUser(user)
                .withOrganisation(organisationId)
                .build();

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(applicationEoiEvidenceResponseRestService.findOneByApplicationId(applicationId)).thenReturn(restSuccess(Optional.of(applicationEoiEvidenceResponseResource)));
        when(fileEntryRestService.findOne(fileEntryId)).thenReturn(restSuccess(fileEntryResource));
        when(competitionEoiEvidenceConfigRestService.getValidFileTypeIdsForEoiEvidence(eoiEvidenceConfigResource.getId())).thenReturn(restSuccess(singletonList(competitionEoiDocumentResource.getFileTypeId())));
        when(fileTypeRestService.findOne(1L)).thenReturn(restSuccess(fileTypeResource));
        when(processRoleRestService.findProcessRole(applicationId)).thenReturn(restSuccess(singletonList(processRoleResource)));
        when(applicationEoiEvidenceResponseRestService.getApplicationEoiEvidenceState(applicationId)).thenReturn(restSuccess(Optional.of(NOT_SUBMITTED)));
        when(processRoleRestService.findProcessRole(user.getId(), applicationId)).thenReturn(restSuccess(processRoleResource));

        TrackViewModel model = populator.populate(applicationId, false, user);

        assertEquals(NOT_SUBMITTED, model.getApplicationEoiEvidenceState());
        assertEquals(application, model.getCurrentApplication());
        assertEquals(fileEntryResource.getName(), model.getEoiEvidenceFileName());
    }

    @Test
    public void populateForNonHecpEoiEvidenceRequired() {

        long applicationId = 1L;
        long organisationId = 2L;
        long competitionId = 100L;

        UserResource user = newUserResource().withRoleGlobal(Role.APPLICANT).build();
        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withEnabledForExpressionOfInterest(false)
                .withCompetitionTypeEnum(GENERIC)
                .build();
        OrganisationResource organisation = newOrganisationResource().withId(organisationId).build();
        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withCompetition(competition.getId())
                .withLeadOrganisationId(organisation.getId())
                .build();
        ProcessRoleResource processRoleResource = newProcessRoleResource()
                .withApplication(applicationId)
                .withRole(ProcessRoleType.LEADAPPLICANT)
                .withUser(user)
                .withOrganisation(organisationId)
                .build();

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(processRoleRestService.findProcessRole(applicationId)).thenReturn(restSuccess(singletonList(processRoleResource)));

        TrackViewModel model = populator.populate(applicationId, true, user);

        assertEquals(application, model.getCurrentApplication());
        assertEquals(competition.getName(), model.getCompetitionName());

    }
}