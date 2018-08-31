package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.competition.domain.CompetitionResearchCategoryLink;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionResearchCategoryLinkBuilder.newCompetitionResearchCategoryLink;

public class CompetitionResearchCategoryLinkRepositoryIntegrationTest extends
        BaseRepositoryIntegrationTest<CompetitionResearchCategoryLinkRepository> {

    @Autowired
    @Override
    protected void setRepository(CompetitionResearchCategoryLinkRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findByCompetitionId() {
        loginCompAdmin();
        Long newCompetitionId = 1L;

        CompetitionResearchCategoryLink link = newCompetitionResearchCategoryLink()
                .withCompetition(newCompetition().withId(newCompetitionId).build())
                .build();

        repository.save(link);

        assertThat(repository.findAllByCompetitionId(newCompetitionId)).contains(link);
    }
}
