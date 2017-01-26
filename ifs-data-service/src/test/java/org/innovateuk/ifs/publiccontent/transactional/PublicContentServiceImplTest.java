package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSection;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentStatus;
import org.innovateuk.ifs.publiccontent.domain.Keyword;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.innovateuk.ifs.publiccontent.mapper.PublicContentMapper;
import org.innovateuk.ifs.publiccontent.repository.KeywordRepository;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.publiccontent.builder.ContentSectionBuilder.newContentSection;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class PublicContentServiceImplTest extends BaseServiceUnitTest<PublicContentServiceImpl> {

    private static final Long COMPETITION_ID = 1L;

    @Mock
    private KeywordRepository keywordRepository;

    @Mock
    private PublicContentRepository publicContentRepository;

    @Mock
    private PublicContentMapper publicContentMapper;

    @Override
    protected PublicContentServiceImpl supplyServiceUnderTest() {
        return new PublicContentServiceImpl();
    }

    @Test
    public void testGetById() {
        PublicContent publicContent = newPublicContent().build();
        PublicContentResource resource = newPublicContentResource().build();
        when(publicContentRepository.findByCompetitionId(COMPETITION_ID)).thenReturn(publicContent);
        when(publicContentMapper.mapToResource(publicContent)).thenReturn(resource);

        ServiceResult<PublicContentResource> result = service.getCompetitionById(COMPETITION_ID);

        assertThat(result.getSuccessObjectOrThrowException(), equalTo(resource));
    }

    @Test
    public void testPublishWithIncompleteSections() {
        PublicContent publicContent = newPublicContent().withContentSections(
                newContentSection().withStatus(PublicContentStatus.IN_PROGRESS).build(2)
        ).build();
        when(publicContentRepository.findByCompetitionId(COMPETITION_ID)).thenReturn(publicContent);

        ServiceResult<Void> result = service.publishByCompetitionId(COMPETITION_ID);

        assertFalse(result.isSuccess());
    }


    @Test
    public void testPublishWithCompleteSections() {
        PublicContent publicContent = newPublicContent().withContentSections(
                newContentSection().withStatus(PublicContentStatus.COMPLETE).build(2)
        ).build();
        when(publicContentRepository.findByCompetitionId(COMPETITION_ID)).thenReturn(publicContent);

        ServiceResult<Void> result = service.publishByCompetitionId(COMPETITION_ID);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testUpdateSectionPublished() {
        PublicContent publicContent = mock(PublicContent.class);
        PublicContentResource publicContentResource = newPublicContentResource().withKeywords(Collections.emptyList()).build();
        when(publicContentMapper.mapToDomain(publicContentResource)).thenReturn(publicContent);
        when(publicContentRepository.save(publicContent)).thenReturn(publicContent);
        when(publicContent.getContentSections()).thenReturn(newContentSection().withStatus(PublicContentStatus.COMPLETE).build(1));
        when(publicContent.getId()).thenReturn(1L);
        when(publicContent.getPublishDate()).thenReturn(LocalDateTime.now());

        ServiceResult<Void> result = service.updateSection(publicContentResource, PublicContentSection.SEARCH);

        verify(publicContentRepository).save(publicContent);
        verify(publicContent).setPublishDate(any());

        assertTrue(result.isSuccess());
    }

    @Test
    public void testUpdateSectionNotPublished() {
        PublicContent publicContent = mock(PublicContent.class);
        PublicContentResource publicContentResource = newPublicContentResource().withKeywords(Collections.emptyList()).build();
        when(publicContentMapper.mapToDomain(publicContentResource)).thenReturn(publicContent);
        when(publicContentRepository.save(publicContent)).thenReturn(publicContent);
        when(publicContent.getContentSections()).thenReturn(newContentSection().withStatus(PublicContentStatus.COMPLETE).build(1));
        when(publicContent.getId()).thenReturn(1L);
        when(publicContent.getPublishDate()).thenReturn(null);

        ServiceResult<Void> result = service.updateSection(publicContentResource, PublicContentSection.SEARCH);

        verify(publicContentRepository).save(publicContent);
        verify(publicContent, never()).setPublishDate(any());

        assertTrue(result.isSuccess());
    }


    @Test
    public void testUpdateSearchSection() {
        PublicContent publicContent = newPublicContent().withContentSections(Collections.emptyList()).build();
        List<String> keywords = asList("key1", "key2");
        PublicContentResource publicContentResource = newPublicContentResource()
                .withKeywords(keywords).build();
        when(publicContentMapper.mapToDomain(publicContentResource)).thenReturn(publicContent);
        when(publicContentRepository.save(publicContent)).thenReturn(publicContent);

        ServiceResult<Void> result = service.updateSection(publicContentResource, PublicContentSection.SEARCH);

        verify(publicContentRepository).save(publicContent);
        keywords.forEach(keyword -> verify(keywordRepository).save(keywordMatcher(keyword)));

        assertTrue(result.isSuccess());
    }

    private static Keyword keywordMatcher(String keyword) {
        return createLambdaMatcher(entity -> {
            return entity.getKeyword().equals(keyword);
        });
    }

}
