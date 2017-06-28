package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentEventResource;
import org.innovateuk.ifs.publiccontent.domain.ContentEvent;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.innovateuk.ifs.publiccontent.mapper.ContentEventMapper;
import org.innovateuk.ifs.publiccontent.repository.ContentEventRepository;
import org.junit.Test;
import org.mockito.Mock;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PUBLIC_CONTENT_IDS_INCONSISTENT;
import static org.innovateuk.ifs.publiccontent.builder.ContentEventBuilder.newContentEvent;
import static org.innovateuk.ifs.publiccontent.builder.ContentEventResourceBuilder.newContentEventResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ContentEventServiceImplTest extends BaseServiceUnitTest<ContentEventServiceImpl> {

    @Mock
    private ContentEventRepository contentEventRepository;

    @Mock
    private ContentEventMapper contentEventMapper;

    @Override
    protected ContentEventServiceImpl supplyServiceUnderTest() {
        return new ContentEventServiceImpl();
    }

    @Test
    public void testResetAndSaveEvents() {
        Long publicContentId = 8L;

        List<ContentEventResource> resources = newContentEventResource()
                .withId(1L)
                .withContent("Content")
                .withDate(ZonedDateTime.of(2017, 1, 1, 1, 1, 0, 0, ZoneId.systemDefault()))
                .withPublicContent(publicContentId)
                .build(2);

        PublicContent publicContent = newPublicContent().withId(publicContentId).build();
        List<ContentEvent> domains = newContentEvent()
                .withId(1L)
                .withContent("Content")
                .withDate(ZonedDateTime.of(2017, 1, 1, 1, 1, 0, 0, ZoneId.systemDefault()))
                .withPublicContent(publicContent)
                .build(2);

        when(contentEventMapper.mapToDomain(resources)).thenReturn(domains);

        ServiceResult<Void> result = service.resetAndSaveEvents(publicContentId, resources);

        assertTrue(result.isSuccess());
        verify(contentEventRepository, times(1)).deleteByPublicContentId(publicContentId);
        verify(contentEventRepository, times(1)).save(domains);
    }

    @Test
    public void testResetAndSaveEventsFailure() {
        Long publicContentId = -18L;

        List<ContentEventResource> resources = newContentEventResource()
                .withId(1L)
                .withContent("Content")
                .withDate(ZonedDateTime.of(2017, 1, 1, 1, 1, 0, 0, ZoneId.systemDefault()))
                .withPublicContent(2L)
                .build(2);

        PublicContent publicContent = newPublicContent().withId(2L).build();
        List<ContentEvent> domains = newContentEvent()
                .withId(1L)
                .withContent("Content")
                .withDate(ZonedDateTime.of(2017, 1, 1, 1, 1, 0, 0, ZoneId.systemDefault()))
                .withPublicContent(publicContent)
                .build(2);

        when(contentEventMapper.mapToDomain(resources)).thenReturn(domains);

        ServiceResult<Void> result = service.resetAndSaveEvents(publicContentId, resources);

        assertTrue(result.isFailure());
        assertEquals(asList(new Error(PUBLIC_CONTENT_IDS_INCONSISTENT)), result.getFailure().getErrors());
        verify(contentEventRepository, never()).deleteByPublicContentId(publicContentId);
        verify(contentEventRepository, never()).save(domains);
    }

    @Test
    public void testResetAndSaveEventsEmptyEvents() {
        Long publicContentId = 8L;

        List<ContentEventResource> resources = Collections.emptyList();

        List<ContentEvent> domains = Collections.emptyList();

        when(contentEventMapper.mapToDomain(resources)).thenReturn(domains);

        ServiceResult<Void> result = service.resetAndSaveEvents(publicContentId, resources);

        assertTrue(result.isSuccess());
        verify(contentEventRepository, times(1)).deleteByPublicContentId(publicContentId);
        verify(contentEventRepository, times(1)).save(domains);
    }
}