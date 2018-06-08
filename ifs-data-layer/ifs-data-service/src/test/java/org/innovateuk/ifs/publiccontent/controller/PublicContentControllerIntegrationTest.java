package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competitionsetup.repository.MilestoneRepository;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competitionsetup.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PublicContentControllerIntegrationTest extends BaseControllerIntegrationTest<PublicContentController> {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private PublicContentRepository publicContentRepository;

    private Competition competition;

    @Override
    @Autowired
    protected void setControllerUnderTest(PublicContentController controller) {
        this.controller = controller;
    }

    @Before
    public void setUp() throws Exception {
        loginCompAdmin();

        competition = competitionRepository.save(newCompetition()
                .with(id(null))
                .build());

        milestoneRepository.save(newMilestone()
                .with(id(null))
                .withCompetition(competition)
                .withType(OPEN_DATE, SUBMISSION_DATE, NOTIFICATIONS)
                .withDate(now().plusDays(1))
                .build(3));
    }

    @Test
    public void testGetByCompetitionId() throws Exception {
        PublicContent publicContent = publicContentRepository.save(newPublicContent()
                .with(id(null))
                .withCompetitionId(competition.getId()).build());

        flushAndClearSession();

        RestResult<PublicContentResource> result = controller.getCompetitionById(competition.getId());

        assertTrue(result.isSuccess());
        assertEquals(publicContent.getId(), result.getSuccess().getId());
    }

    @Test
    public void testPublishByCompetitionId() throws Exception {
        ZonedDateTime oldPublishDate = now().minusYears(1);

        publicContentRepository.save(newPublicContent()
                .with(id(null))
                .withPublishDate(oldPublishDate)
                .withCompetitionId(competition.getId()).build());

        flushAndClearSession();

        RestResult<Void> result = controller.publishByCompetition(competition.getId());

        assertTrue(result.isSuccess());

        PublicContent publicContent = publicContentRepository.findByCompetitionId(competition.getId());
        assertTrue(publicContent.getPublishDate().isAfter(oldPublishDate));
    }
}
