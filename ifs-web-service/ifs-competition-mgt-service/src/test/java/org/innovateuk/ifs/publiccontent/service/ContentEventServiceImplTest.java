package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.competition.publiccontent.resource.ContentEventResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.publiccontent.builder.ContentEventResourceBuilder.newContentEventResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.mockito.Mockito.when;

/**
 * Tests for public content events web service.
 */
@RunWith(MockitoJUnitRunner.class)
public class ContentEventServiceImplTest {

    @InjectMocks
    private ContentEventServiceImpl target;

    @Mock
    private ContentEventRestService restService;

    @Test
    public void testUpdateEvent() {
        ContentEventResource resource = newContentEventResource().build();
        when(restService.saveEvent(resource)).thenReturn(restSuccess());

        target.updateEvent(resource).getSuccessObjectOrThrowException();
    }

    @Test
    public void testResetAndSaveEvents() {
        Long publicContentId = 4L;
        PublicContentResource publicContent = newPublicContentResource()
                .with(publicContentResource -> publicContentResource.setId(publicContentId))
                .build();
        List<ContentEventResource> resources = newContentEventResource().withPublicContent(publicContentId).build(5);

        when(restService.resetAndSaveEvents(publicContentId, resources)).thenReturn(restSuccess());

        target.resetAndSaveEvents(publicContent, resources).getSuccessObjectOrThrowException();
    }
}
