package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionFunder;
import org.innovateuk.ifs.competition.repository.CompetitionFunderRepository;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for operations around the usage and processing of Competitions
 */
@Service
public class CompetitionFunderServiceImpl extends BaseTransactionalService implements CompetitionFunderService {
    
	private static final Log LOG = LogFactory.getLog(CompetitionFunderServiceImpl.class);

    @Autowired
    private CompetitionFunderRepository competitionFunderRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Override
    public void reinsertFunders(CompetitionResource resource) {
        Competition competition = competitionRepository.findById(resource.getId());

        competitionFunderRepository.deleteByCompetitionId(resource.getId());
        resource.getFunders().forEach(funderResource -> {
            CompetitionFunder competitionFunder = new CompetitionFunder();
            competitionFunder.setFunder(funderResource.getFunder());
            competitionFunder.setFunderBudget(funderResource.getFunderBudget());
            competitionFunder.setCoFunder(funderResource.getCoFunder());
            competitionFunder.setCompetition(competition);
            competitionFunderRepository.save(competitionFunder);
        });


    }


}
