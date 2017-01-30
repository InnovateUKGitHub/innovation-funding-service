package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentStatus;
import org.innovateuk.ifs.publiccontent.domain.ContentSection;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.innovateuk.ifs.publiccontent.mapper.PublicContentMapper;
import org.innovateuk.ifs.publiccontent.repository.ContentSectionRepository;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.publiccontent.builder.ContentSectionBuilder.newContentSection;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PublicContentServiceImplTest extends BaseServiceUnitTest<PublicContentServiceImpl> {

    private static final Long COMPETITION_ID = 1L;

    @Mock
    private PublicContentRepository publicContentRepository;

    @Mock
    private ContentSectionRepository contentSectionRepository;

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

        ServiceResult<PublicContentResource> result = service.findByCompetitionId(COMPETITION_ID);

        assertThat(result.getSuccessObjectOrThrowException(), equalTo(resource));
        verify(publicContentRepository).findByCompetitionId(COMPETITION_ID);
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
    public void testPublishWithIncompleteSections() {
        PublicContent publicContent = newPublicContent().withContentSections(
                newContentSection().withStatus(PublicContentStatus.IN_PROGRESS).build(2)
        ).build();
        when(publicContentRepository.findByCompetitionId(COMPETITION_ID)).thenReturn(publicContent);

        ServiceResult<Void> result = service.publishByCompetitionId(COMPETITION_ID);

        assertFalse(result.isSuccess());
        verify(publicContentRepository).findByCompetitionId(COMPETITION_ID);
    }


    @Test
    public void testPublishWithCompleteSections() {
        PublicContent publicContent = newPublicContent().withContentSections(
                newContentSection().withStatus(PublicContentStatus.COMPLETE).build(2)
        ).build();
        when(publicContentRepository.findByCompetitionId(COMPETITION_ID)).thenReturn(publicContent);

        ServiceResult<Void> result = service.publishByCompetitionId(COMPETITION_ID);

        assertTrue(result.isSuccess());
        verify(publicContentRepository).findByCompetitionId(COMPETITION_ID);
    }


}
