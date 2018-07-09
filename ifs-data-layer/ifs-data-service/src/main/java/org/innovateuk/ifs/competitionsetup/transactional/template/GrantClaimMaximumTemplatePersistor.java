package org.innovateuk.ifs.competitionsetup.transactional.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.transactional.template.BaseChainedTemplatePersistor;
import org.innovateuk.ifs.finance.domain.GrantClaimMaximum;
import org.innovateuk.ifs.finance.repository.GrantClaimMaximumRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * Transactional component providing functions for persisting copies of GrantClaimMaximums by their parent
 * Competition entity object.
 */
@Component
public class GrantClaimMaximumTemplatePersistor implements BaseChainedTemplatePersistor<List<GrantClaimMaximum>,
        Competition> {

    private GrantClaimMaximumRepository grantClaimMaximumRepository;

    public GrantClaimMaximumTemplatePersistor(GrantClaimMaximumRepository grantClaimMaximumRepository) {
        this.grantClaimMaximumRepository = grantClaimMaximumRepository;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public List<GrantClaimMaximum> persistByParentEntity(Competition competition) {
        return competition.getGrantClaimMaximums()
                .stream()
                .map(createGrantClaimMaximum(competition))
                .collect(toList());
    }

    @Transactional
    @Override
    public void cleanForParentEntity(Competition competition) {
        competition.getGrantClaimMaximums().forEach(grantClaimMaximum -> {
            entityManager.detach(grantClaimMaximum);
            grantClaimMaximumRepository.delete(grantClaimMaximum);
        });
    }

    private Function<GrantClaimMaximum, GrantClaimMaximum> createGrantClaimMaximum(Competition competition) {
        return (GrantClaimMaximum grantClaimMaximum) -> {
            entityManager.detach(grantClaimMaximum);
            grantClaimMaximum.setCompetition(competition);
            grantClaimMaximum.setId(null);
            return grantClaimMaximumRepository.save(grantClaimMaximum);
        };
    }
}
