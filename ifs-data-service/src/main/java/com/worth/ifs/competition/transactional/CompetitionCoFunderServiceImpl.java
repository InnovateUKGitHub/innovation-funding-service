package com.worth.ifs.competition.transactional;

import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.domain.CompetitionFunder;
import com.worth.ifs.competition.repository.CompetitionCoFunderRepository;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for operations around the usage and processing of Competitions
 */
@Service
public class CompetitionCoFunderServiceImpl extends BaseTransactionalService implements CompetitionCoFunderService {
    
	private static final Log LOG = LogFactory.getLog(CompetitionCoFunderServiceImpl.class);

    @Autowired
    private CompetitionCoFunderRepository competitionCoFunderRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Override
    public void reinsertCoFunders(CompetitionResource resource) {
        Competition competition = competitionRepository.findById(resource.getId());

        competitionCoFunderRepository.deleteByCompetitionId(resource.getId());
        resource.getFunders().forEach(coFunderResource -> {
            CompetitionFunder competitionFunder = new CompetitionFunder();
            competitionFunder.setFunder(coFunderResource.getFunder());
            competitionFunder.setFunderBudget(coFunderResource.getFunderBudget());
            competitionFunder.setCompetition(competition);
            competitionCoFunderRepository.save(competitionFunder);
        });


    }


}
