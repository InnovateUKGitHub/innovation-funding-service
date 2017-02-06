package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentEventResource;
import org.innovateuk.ifs.publiccontent.domain.ContentEvent;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.innovateuk.ifs.publiccontent.mapper.ContentEventMapper;
import org.innovateuk.ifs.publiccontent.mapper.PublicContentMapper;
import org.innovateuk.ifs.publiccontent.repository.ContentEventRepository;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentEventResourceBuilder.newPublicContentEventResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Controller integration test for public content events
 */
public class PublicContentEventControllerIntegrationTest extends BaseControllerIntegrationTest<PublicContentEventController> {
    @Autowired
    private PublicContentRepository publicContentRepository;

    @Autowired
    private PublicContentMapper publicContentMapper;

    @Autowired
    private ContentEventRepository contentEventRepository;

    @Autowired
    private ContentEventMapper contentEventMapper;

    @Override
    @Autowired
    protected void setControllerUnderTest(PublicContentEventController controller) {
        this.controller = controller;
    }

    @Test
    @Rollback
    public void saveEvent() throws Exception {
        PublicContent publicContent = publicContentRepository.save(newPublicContent()
                .withPublishDate(LocalDateTime.now())
                .withCompetitionId(1L)
                .build());

        PublicContentEventResource event = newPublicContentEventResource()
                .withContent("New event")
                .withPublicContent(publicContent.getId())
                .withDate(LocalDateTime.now()).build();

        loginCompAdmin();
        RestResult<Void> result = controller.saveEvent(event);

        assertTrue(result.isSuccess());
    }

    @Test
    @Rollback
    public void resetAndSaveEvent() throws Exception {
        PublicContent publicContent = publicContentRepository.save(newPublicContent()
                .withPublishDate(LocalDateTime.now())
                .withCompetitionId(1L)
                .build());

        PublicContentEventResource event = newPublicContentEventResource()
                .withContent("New event")
                .withPublicContent(publicContent.getId())
                .withDate(LocalDateTime.of(2017,2,6,11,20,23)).build();

        loginCompAdmin();
        RestResult<Void> result = controller.resetAndSaveEvent(1L, asList(event));

        flushAndClearSession();

        assertTrue(result.isSuccess());

        List<PublicContentEventResource> expectedEvents = asList(event);
        List<ContentEvent> resultEvents = publicContentRepository.findOne(publicContent.getId()).getContentEvents();
        assertEquals(expectedEvents.get(0).getDate(), resultEvents.get(0).getDate());
        assertEquals(expectedEvents.get(0).getContent(), resultEvents.get(0).getContent());
    }
}