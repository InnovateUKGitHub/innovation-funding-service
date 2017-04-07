package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.*;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupService;
import org.innovateuk.ifs.competition.transactional.MilestoneService;
import org.innovateuk.ifs.publiccontent.domain.ContentSection;
import org.innovateuk.ifs.publiccontent.domain.Keyword;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.innovateuk.ifs.publiccontent.mapper.PublicContentMapper;
import org.innovateuk.ifs.publiccontent.repository.ContentSectionRepository;
import org.innovateuk.ifs.publiccontent.repository.KeywordRepository;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.publiccontent.resource.PublicContentStatus.COMPLETE;
import static org.innovateuk.ifs.competition.publiccontent.resource.PublicContentStatus.IN_PROGRESS;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.*;
import static org.innovateuk.ifs.publiccontent.builder.ContentEventResourceBuilder.newContentEventResource;
import static org.innovateuk.ifs.publiccontent.builder.ContentGroupResourceBuilder.newContentGroupResource;
import static org.innovateuk.ifs.publiccontent.builder.ContentSectionBuilder.newContentSection;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentSectionResourceBuilder.newPublicContentSectionResource;
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
    private ContentSectionRepository contentSectionRepository;

    @Mock
    private PublicContentMapper publicContentMapper;

    @Mock
    private ContentGroupService contentGroupService;

    @Mock
    private ContentEventService contentEventService;

    @Mock
    private MilestoneService milestoneService;

    @Mock
    private CompetitionSetupService competitionSetupService;

    @Override
    protected PublicContentServiceImpl supplyServiceUnderTest() {
        return new PublicContentServiceImpl();
    }


    private static final List<ContentGroupResource> CONTENT_GROUPS = asList(
            newContentGroupResource().withPriority(2).build(),
            newContentGroupResource().withPriority(1).build());

    private static final List<PublicContentSectionResource> COMPLETE_SECTIONS =
            stream(PublicContentSectionType.values()).map(type -> newPublicContentSectionResource()
                    .withType(type)
                    .withStatus(PublicContentStatus.COMPLETE)
                    .withContentGroups(CONTENT_GROUPS)
                    .build()).collect(Collectors.toList());


    @Test
    public void testGetById() {
        PublicContent publicContent = newPublicContent().build();
        PublicContentResource resource = newPublicContentResource().withContentSections(COMPLETE_SECTIONS).build();
        when(publicContentRepository.findByCompetitionId(COMPETITION_ID)).thenReturn(publicContent);
        when(publicContentMapper.mapToResource(publicContent)).thenReturn(resource);

        ServiceResult<PublicContentResource> result = service.findByCompetitionId(COMPETITION_ID);

        assertThat(result.getSuccessObjectOrThrowException(), equalTo(resource));
        verify(publicContentRepository).findByCompetitionId(COMPETITION_ID);
        assertTrue(isSortedByPriority(result.getSuccessObjectOrThrowException()));
    }


    @Test
    public void testInitialise() {
        when(publicContentRepository.findByCompetitionId(COMPETITION_ID)).thenReturn(null);

        ServiceResult<Void> result = service.initialiseByCompetitionId(COMPETITION_ID);

        assertTrue(result.isSuccess());
        verify(publicContentRepository).save(Mockito.<PublicContent>any());
        verify(contentSectionRepository, times(asList(PublicContentSectionType.values()).size())).save(Mockito.<ContentSection>any());
    }

    @Test
    public void testInitialiseFailure() {
        when(publicContentRepository.findByCompetitionId(COMPETITION_ID)).thenReturn(new PublicContent());

        ServiceResult<Void> result = service.initialiseByCompetitionId(COMPETITION_ID);

        assertFalse(result.isSuccess());
        verify(publicContentRepository).findByCompetitionId(COMPETITION_ID);
        verifyNoMoreInteractions(publicContentRepository);
        verifyZeroInteractions(contentSectionRepository);
    }

    @Test
    public void testPublishWithIncompleteSectionsFailure() {
        PublicContent publicContent = newPublicContent().withCompetitionId(COMPETITION_ID).withContentSections(
                newContentSection().withStatus(IN_PROGRESS).build(2)
        ).build();
        when(publicContentRepository.findByCompetitionId(COMPETITION_ID)).thenReturn(publicContent);
        mockPublicMilestonesValid(true);

        ServiceResult<Void> result = service.publishByCompetitionId(COMPETITION_ID);

        assertFalse(result.isSuccess());
        verify(publicContentRepository).findByCompetitionId(COMPETITION_ID);
    }
    @Test
    public void testPublishWithMissingDatesFailure() {
        PublicContent publicContent = newPublicContent().withCompetitionId(COMPETITION_ID).withContentSections(
                newContentSection().withStatus(PublicContentStatus.COMPLETE).build(2)
        ).build();
        when(publicContentRepository.findByCompetitionId(COMPETITION_ID)).thenReturn(publicContent);
        mockPublicMilestonesValid(false);


        ServiceResult<Void> result = service.publishByCompetitionId(COMPETITION_ID);

        assertFalse(result.isSuccess());
        verify(publicContentRepository).findByCompetitionId(COMPETITION_ID);
    }

    @Test
    public void testPublishWithCompleteSectionsAndDatesSuccess() {
        PublicContent publicContent = newPublicContent().withCompetitionId(COMPETITION_ID).withContentSections(
                newContentSection().withStatus(PublicContentStatus.COMPLETE).build(2)
        ).build();
        when(publicContentRepository.findByCompetitionId(COMPETITION_ID)).thenReturn(publicContent);
        when(competitionSetupService.markSectionComplete(COMPETITION_ID, CONTENT)).thenReturn(serviceSuccess());
        mockPublicMilestonesValid(true);

        ServiceResult<Void> result = service.publishByCompetitionId(COMPETITION_ID);

        assertTrue(result.isSuccess());
        verify(publicContentRepository).findByCompetitionId(COMPETITION_ID);
    }


    @Test
    public void testUpdateSectionPublished() {
        PublicContent publicContent = mock(PublicContent.class);
        PublicContentResource publicContentResource = newPublicContentResource().withKeywords(Collections.emptyList()).build();
        when(publicContentMapper.mapToDomain(publicContentResource)).thenReturn(publicContent);
        when(publicContentRepository.save(publicContent)).thenReturn(publicContent);
        when(publicContent.getContentSections()).thenReturn(newContentSection().withStatus(PublicContentStatus.COMPLETE).build(1));
        when(publicContent.getId()).thenReturn(1L);
        when(publicContent.getCompetitionId()).thenReturn(COMPETITION_ID);
        when(publicContent.getPublishDate()).thenReturn(ZonedDateTime.now());
        mockPublicMilestonesValid(true);

        ServiceResult<Void> result = service.updateSection(publicContentResource, PublicContentSectionType.SEARCH);

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
        ContentSection section = newContentSection().withType(PublicContentSectionType.SEARCH).withStatus(IN_PROGRESS).build();
        when(publicContent.getContentSections()).thenReturn(asList(section));
        when(publicContent.getId()).thenReturn(1L);
        when(publicContent.getPublishDate()).thenReturn(null);

        ServiceResult<Void> result = service.updateSection(publicContentResource, PublicContentSectionType.SEARCH);

        verify(publicContentRepository).save(publicContent);
        verify(publicContent, never()).setPublishDate(any());
        //Assert that it doesn't change the status to complete.
        assertThat(section.getStatus(), equalTo(IN_PROGRESS));

        assertTrue(result.isSuccess());
    }

    @Test
    public void testMarkAsComplete() {
        PublicContent publicContent = mock(PublicContent.class);
        PublicContentResource publicContentResource = newPublicContentResource().withKeywords(Collections.emptyList()).build();
        when(publicContentMapper.mapToDomain(publicContentResource)).thenReturn(publicContent);
        when(publicContentRepository.save(publicContent)).thenReturn(publicContent);
        ContentSection section = newContentSection().withType(PublicContentSectionType.SEARCH).withStatus(IN_PROGRESS).build();
        when(publicContent.getContentSections()).thenReturn(asList(section));
        when(publicContent.getId()).thenReturn(1L);
        when(publicContent.getPublishDate()).thenReturn(null);

        ServiceResult<Void> result = service.markSectionAsComplete(publicContentResource, PublicContentSectionType.SEARCH);

        //Assert that it changes status to complete
        assertThat(section.getStatus(), equalTo(COMPLETE));

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

        ServiceResult<Void> result = service.updateSection(publicContentResource, PublicContentSectionType.SEARCH);

        verify(publicContentRepository).save(publicContent);
        keywords.forEach(keyword -> verify(keywordRepository).save(keywordMatcher(keyword)));

        assertTrue(result.isSuccess());
    }

    @Test
    public void testUpdateDatesSection() {
        PublicContent publicContent = newPublicContent().withContentSections(Collections.emptyList()).build();
        List<ContentEventResource> events = newContentEventResource().build(1);
        PublicContentResource publicContentResource = newPublicContentResource()
                .withContentEvents(events).build();
        when(publicContentMapper.mapToDomain(publicContentResource)).thenReturn(publicContent);
        when(publicContentRepository.save(publicContent)).thenReturn(publicContent);
        when(contentEventService.resetAndSaveEvents(publicContent.getId(), events)).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.updateSection(publicContentResource, PublicContentSectionType.DATES);

        verify(publicContentRepository).save(publicContent);
        verify(contentEventService).resetAndSaveEvents(publicContent.getId(), events);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testUpdateEligibilitySection() {
        testUpdateContentGroupSection(PublicContentSectionType.ELIGIBILITY);
    }

    @Test
    public void testUpdateSupportingInformationSection() {
        testUpdateContentGroupSection(PublicContentSectionType.SUPPORTING_INFORMATION);
    }

    @Test
    public void testUpdateScopeSection() {
        testUpdateContentGroupSection(PublicContentSectionType.SCOPE);
    }

    @Test
    public void testUpdateHowToApplySection() {
        testUpdateContentGroupSection(PublicContentSectionType.HOW_TO_APPLY);
    }

    private void testUpdateContentGroupSection(PublicContentSectionType type) {
        PublicContent publicContent = newPublicContent().withContentSections(Collections.emptyList()).build();
        PublicContentResource publicContentResource = newPublicContentResource()
                .withContentSections(COMPLETE_SECTIONS).build();
        when(publicContentMapper.mapToDomain(publicContentResource)).thenReturn(publicContent);
        when(publicContentRepository.save(publicContent)).thenReturn(publicContent);
        when(contentGroupService.saveContentGroups(publicContentResource, publicContent, type)).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.updateSection(publicContentResource, type);

        verify(publicContentRepository).save(publicContent);
        verify(contentGroupService).saveContentGroups(publicContentResource, publicContent, type);

        assertTrue(result.isSuccess());
    }

    private boolean isSortedByPriority(PublicContentResource publicContentResource) {
        return publicContentResource.getContentSections().stream().map(contentSectionResource ->
                contentSectionResource.getContentGroups().get(0).getPriority() < contentSectionResource.getContentGroups().get(1).getPriority())
        .anyMatch(Boolean::booleanValue);
    }

    private static Keyword keywordMatcher(String keyword) {
        return createLambdaMatcher(entity -> {
            return entity.getKeyword().equals(keyword);
        });
    }

    private void mockPublicMilestonesValid(boolean valid) {
        when(milestoneService.allPublicDatesComplete(COMPETITION_ID))
                .thenReturn(serviceSuccess(valid));
    }
}
