package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Tests for public content web service.
 */
@RunWith(MockitoJUnitRunner.class)
public class PublicContentServiceImplTest {

    private static final Long COMPETITION_ID = 1L;

    @InjectMocks
    private PublicContentServiceImpl target;

    @Mock
    private PublicContentRestService publicContentRestService;


    @Test
    public void testUpdateSection() {
        PublicContentResource resource = newPublicContentResource().build();
        PublicContentSectionType type = PublicContentSectionType.ELIGIBILITY;
        when(publicContentRestService.updateSection(resource, type)).thenReturn(restSuccess());

        target.updateSection(resource, type).getSuccess();
    }

    @Test
    public void testMarkSectionAsComplete() {
        PublicContentResource resource = newPublicContentResource().build();
        PublicContentSectionType type = PublicContentSectionType.ELIGIBILITY;
        when(publicContentRestService.markSectionAsComplete(resource, type)).thenReturn(restSuccess());

        target.markSectionAsComplete(resource, type).getSuccess();
    }

    @Test
    public void testGetCompetitionById() {
        PublicContentResource resource = newPublicContentResource().build();
        when(publicContentRestService.getByCompetitionId(COMPETITION_ID)).thenReturn(restSuccess(resource));

        PublicContentResource result = target.getCompetitionById(COMPETITION_ID);

        assertThat(result, equalTo(resource));
    }

    @Test
    public void testPublishByCompetitionId() {
        when(publicContentRestService.publishByCompetitionId(COMPETITION_ID)).thenReturn(restSuccess());

        ServiceResult<Void> result = target.publishByCompetitionId(COMPETITION_ID);

        assertTrue(result.isSuccess());
    }
}
