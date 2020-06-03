package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.PostAwardService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class CompetitionSetupPostAwardServiceServiceImpl extends BaseTransactionalService implements CompetitionSetupPostAwardServiceService {

    private ServiceResult<Competition> findCompetitionById(long id) {
        return find(competitionRepository.findById(id), notFoundError(Competition.class, id));
    }

    @Override
    @Transactional
    public ServiceResult<Void> configurePostAwardService(long competitionId, PostAwardService postAwardService) {
        return findCompetitionById(competitionId)
                .andOnSuccessReturnVoid(competition -> {
                        competition.setPostAwardService(postAwardService);
                        competitionRepository.save(competition);
                    });
    }
}
