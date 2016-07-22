package com.worth.ifs.competition.transactional;

import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.domain.CompetitionCoFunder;
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
        resource.getCoFunders().forEach(coFunderResource -> {
            CompetitionCoFunder competitionCoFunder = new CompetitionCoFunder();
            competitionCoFunder.setCoFunder(coFunderResource.getCoFunder());
            competitionCoFunder.setCoFunderBudget(coFunderResource.getCoFunderBudget());
            competitionCoFunder.setCompetition(competition);
            competitionCoFunderRepository.save(competitionCoFunder);
        });


    }


}
