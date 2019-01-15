package org.innovateuk.ifs.publiccontent.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.GRANT;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

// TODO IFS-5010 remove along with the triggers
public class PublicContentRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<PublicContentRepository> {

    @Autowired
    @Override
    protected void setRepository(PublicContentRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private CompetitionRepository competitionRepository;

    @Test
    public void testDbUpdateTrigger() throws InterruptedException {
        loginIfsAdmin();
        Competition competition = newCompetition().with(id(null)).build();
        competition = competitionRepository.save(competition);
        flushAndClearSession();

        PublicContent publicContent = newPublicContent()
                .with(id(null))
                .withCompetitionId(competition.getId())
                .build();
        repository.save(publicContent);
        flushAndClearSession();

        em.createNativeQuery(String.format("UPDATE public_content SET funding_type='GRANT' where competition_id=%d;", competition.getId()))
                .executeUpdate();
        flushAndClearSession();

        Competition actual = competitionRepository.findById(competition.getId()).get();

        assertEquals(FundingType.GRANT, actual.getFundingType());
    }

    @Test
    public void testDbUpdateTrigger_null() {
        loginIfsAdmin();
        Competition competition = newCompetition().withFundingType(GRANT).with(id(null)).build();
        competitionRepository.save(competition);
        flushAndClearSession();

        PublicContent publicContent = newPublicContent()
                .with(id(null))
                .withCompetitionId(competition.getId())
                .build();
        repository.save(publicContent);
        flushAndClearSession();

        Competition actual = competitionRepository.findById(competition.getId()).get();

        assertNotNull(actual.getFundingType());
    }
}