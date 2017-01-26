package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentStatus;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.innovateuk.ifs.publiccontent.mapper.PublicContentMapper;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.publiccontent.builder.ContentSectionBuilder.newContentSection;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class PublicContentServiceImplTest extends BaseServiceUnitTest<PublicContentServiceImpl> {

    private static final Long COMPETITION_ID = 1L;


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


}
