package org.innovateuk.ifs.publiccontent.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PublicContentRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<PublicContentRepository> {

    @Autowired
    @Override
    protected void setRepository(PublicContentRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private CompetitionRepository competitionRepository;

    @Test
    // TODO IFS-4982 remove along with the triggers
    public void testDbUpdateTrigger() {
        loginIfsAdmin();
        Competition competition = newCompetition().with(id(null)).build();
        competitionRepository.save(competition);
        flushAndClearSession();

        assertNull(competition.getFundingType());

        PublicContent publicContent = newPublicContent()
                .with(id(null))
                .withCompetitionId(competition.getId())
                .build();
        repository.save(publicContent);
        flushAndClearSession();

        Competition actual = competitionRepository.findById(competition.getId());

        assertEquals(publicContent.getFundingType(), actual.getFundingType());
    }
}
