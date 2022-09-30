package org.innovateuk.ifs.application.forms.populator;

import org.innovateuk.ifs.application.forms.form.EoiEvidenceForm;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.service.ApplicationEoiEvidenceResponseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class EoiEvidenceFormPopulatorTest {

    @InjectMocks
    private EoiEvidenceFormPopulator populator;

    @Mock
    private ApplicationEoiEvidenceResponseRestService applicationEoiEvidenceResponseRestService;

    @Mock
    private FileEntryRestService fileEntryRestService;

    @Test
    public void populate() {
        long applicationId = 1L;
        long organisationId = 2L;
        long fileEntryId = 3L;

        ApplicationEoiEvidenceResponseResource responseResource = ApplicationEoiEvidenceResponseResource.builder()
                .applicationId(applicationId)
                .organisationId(organisationId)
                .fileEntryId(fileEntryId)
                .build();
        FileEntryResource fileEntryResource = newFileEntryResource().withId(fileEntryId).withName("Eoi evidence file").build();
        when(applicationEoiEvidenceResponseRestService.findOneByApplicationId(applicationId)).thenReturn(restSuccess(Optional.of(responseResource)));
        when(fileEntryRestService.findOne(fileEntryId)).thenReturn(restSuccess(fileEntryResource));

        EoiEvidenceForm result = populator.populate(applicationId);

       assertEquals(fileEntryResource.getName(), result.getEvidenceFileEntryName());
    }
}