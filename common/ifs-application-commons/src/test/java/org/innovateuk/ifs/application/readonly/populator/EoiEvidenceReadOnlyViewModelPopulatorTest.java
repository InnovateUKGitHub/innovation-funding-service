package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.EoiEvidenceReadOnlyViewModel;
import org.innovateuk.ifs.application.service.ApplicationEoiEvidenceResponseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;
import org.innovateuk.ifs.competition.service.CompetitionEoiEvidenceConfigRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationExpressionOfInterestConfigResourceBuilder.newApplicationExpressionOfInterestConfigResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class EoiEvidenceReadOnlyViewModelPopulatorTest {

    @InjectMocks
    private EoiEvidenceReadOnlyViewModelPopulator eoiEvidenceReadOnlyViewModelPopulator;

    @Mock
    private CompetitionEoiEvidenceConfigRestService competitionEoiEvidenceConfigRestService;

    @Mock
    private ApplicationEoiEvidenceResponseRestService applicationEoiEvidenceResponseRestService;

    @Mock
    private FileEntryRestService fileEntryRestService;

    @Test
    public void populate() {

        Long competitionId = 1L;
        Long applicationId = 2L;
        Long organisationId = 3L;
        Long fileEntryId = 4L;
        String title = "title";
        String guidance = "guidance";
        String fileName = "Filename";

        ApplicationResource applicationResource = newApplicationResource()
                .withCompetition(competitionId)
                .withApplicationExpressionOfInterestConfigResource(newApplicationExpressionOfInterestConfigResource()
                        .withEnabledForExpressionOfInterest(true)
                        .build())
                .build();

        CompetitionEoiEvidenceConfigResource competitionEoiEvidenceConfigResource = CompetitionEoiEvidenceConfigResource.builder()
                .evidenceRequired(true)
                .evidenceTitle(title)
                .evidenceGuidance(guidance)
                .build();

        ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource = ApplicationEoiEvidenceResponseResource.builder()
                .applicationId(applicationId)
                .organisationId(organisationId)
                .fileEntryId(fileEntryId)
                .fileState(State.SUBMITTED)
                .build();

        FileEntryResource fileEntryResource = newFileEntryResource()
                .withName(fileName)
                .withFilesizeBytes(4380)
                .build();

        when(competitionEoiEvidenceConfigRestService.findByCompetitionId(competitionId)).thenReturn(restSuccess(competitionEoiEvidenceConfigResource));
        when(applicationEoiEvidenceResponseRestService.findOneByApplicationId(applicationId)).thenReturn(restSuccess(Optional.of(applicationEoiEvidenceResponseResource)));
        when(fileEntryRestService.findOne(fileEntryId)).thenReturn(restSuccess(fileEntryResource));

        EoiEvidenceReadOnlyViewModel viewModel = eoiEvidenceReadOnlyViewModelPopulator.populate(applicationResource);

        assertNotNull(viewModel);
        assertTrue(viewModel.isExpressionOfInterestApplication());
        assertEquals(title, viewModel.getTitle());
        assertEquals(applicationId, viewModel.getApplicationId());
        assertEquals(fileName, viewModel.getName());
        assertEquals("4KB", viewModel.getHumanReadableFileSize());
    }
}
