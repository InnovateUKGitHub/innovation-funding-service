package org.innovateuk.ifs.competitionsetup.transactional.template;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.GrantClaimMaximum;
import org.innovateuk.ifs.finance.repository.GrantClaimMaximumRepository;
import org.junit.Test;
import org.mockito.Mock;

import javax.persistence.EntityManager;
import java.util.List;

import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.domain.builder.GrantClaimMaximumBuilder.newGrantClaimMaximum;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

public class GrantClaimMaximumTemplatePersistorTest extends BaseServiceUnitTest<GrantClaimMaximumTemplatePersistor> {

    @Mock
    private GrantClaimMaximumRepository grantClaimMaximumRepository;

    @Mock
    private EntityManager entityManager;

    @Override
    protected GrantClaimMaximumTemplatePersistor supplyServiceUnderTest() {
        return new GrantClaimMaximumTemplatePersistor(grantClaimMaximumRepository);
    }

    @Test
    public void persistByParentEntity() {
        Competition competition = newCompetition()
                .build();

        List<GrantClaimMaximum> grantClaimMaximums = newGrantClaimMaximum()
                .withCompetition(competition)
                .withDef(50, 100)
                .withSmall(15, 30)
                .withMedium(30, 60)
                .withLarge(45, 90)
                .build(2);

        competition.setGrantClaimMaximums(grantClaimMaximums);

        service.persistByParentEntity(competition);

        grantClaimMaximums.forEach(grantClaimMaximum -> {
            verify(entityManager).detach(grantClaimMaximum);
            verify(grantClaimMaximumRepository).save(createGrantClaimMaximumExpectations(grantClaimMaximum,
                    competition));
        });
    }

    @Test
    public void cleanForParentEntity() {
        Competition competition = newCompetition()
                .build();

        List<GrantClaimMaximum> grantClaimMaximums = newGrantClaimMaximum()
                .withCompetition(competition)
                .withDef(50, 100)
                .withSmall(15, 30)
                .withMedium(30, 60)
                .withLarge(45, 90)
                .build(2);

        competition.setGrantClaimMaximums(grantClaimMaximums);

        service.cleanForParentEntity(competition);

        grantClaimMaximums.forEach(grantClaimMaximum -> {
            verify(entityManager).detach(grantClaimMaximum);
            verify(grantClaimMaximumRepository).delete(grantClaimMaximum);
        });
    }

    private GrantClaimMaximum createGrantClaimMaximumExpectations(GrantClaimMaximum grantClaimMaximum, Competition
            competition) {
        return createLambdaMatcher(actual -> {
            assertNull(actual.getId());
            assertEquals(competition, actual.getCompetition());
            assertEquals(grantClaimMaximum.getDef(), actual.getDef());
            assertEquals(grantClaimMaximum.getSmall(), actual.getSmall());
            assertEquals(grantClaimMaximum.getMedium(), actual.getMedium());
            assertEquals(grantClaimMaximum.getLarge(), actual.getLarge());
        });
    }
}