package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.time.ZonedDateTime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class PublicContentControllerIntegrationTest extends BaseControllerIntegrationTest<PublicContentController> {
    private static final Long COMPETITION_ID = 1L;

    @Autowired
    private PublicContentRepository publicContentRepository;

    @Override
    @Autowired
    protected void setControllerUnderTest(PublicContentController controller) {
        this.controller = controller;
    }


    @Before
    public void setLoggedInUserOnThread() {
        loginCompAdmin();
    }


    @Test
    @Rollback
    public void testGetByCompetitionId() throws Exception {
        PublicContent publicContent = publicContentRepository.save(PublicContentBuilder.newPublicContent().withCompetitionId(COMPETITION_ID).build());
        flushAndClearSession();

        RestResult<PublicContentResource> result = controller.getCompetitionById(COMPETITION_ID);

        assertTrue(result.isSuccess());

        assertThat(publicContent.getId(), equalTo(result.getSuccessObjectOrThrowException().getId()));
    }

    @Test
    @Rollback
    public void testPublishByCompetitionId() throws Exception {
        ZonedDateTime oldPublishDate = ZonedDateTime.now().minusYears(1);
        publicContentRepository.save(PublicContentBuilder.newPublicContent()
                .withPublishDate(oldPublishDate)
                .withCompetitionId(COMPETITION_ID).build());
        flushAndClearSession();

        RestResult<Void> result = controller.publishByCompetition(COMPETITION_ID);

        assertTrue(result.isSuccess());
        PublicContent publicContent =  publicContentRepository.findByCompetitionId(COMPETITION_ID);
        assertTrue(publicContent.getPublishDate().isAfter(oldPublishDate));
    }
}
