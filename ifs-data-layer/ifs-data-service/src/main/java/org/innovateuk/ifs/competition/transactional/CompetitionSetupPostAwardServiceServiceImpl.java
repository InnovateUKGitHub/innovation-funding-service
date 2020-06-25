package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionPostAwardServiceResource;
import org.innovateuk.ifs.competition.resource.PostAwardService;
import org.innovateuk.ifs.grant.domain.GrantProcessConfiguration;
import org.innovateuk.ifs.grant.repository.GrantProcessConfigurationRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class CompetitionSetupPostAwardServiceServiceImpl extends BaseTransactionalService implements CompetitionSetupPostAwardServiceService {

    @Autowired
    private GrantProcessConfigurationRepository grantProcessConfigurationRepository;

    private ServiceResult<Competition> findCompetitionById(long id) {
        return find(competitionRepository.findById(id), notFoundError(Competition.class, id));
    }

    @Override
    @Transactional
    public ServiceResult<Void> configurePostAwardService(long competitionId, PostAwardService postAwardService) {

        boolean sendByDefault = (postAwardService == PostAwardService.IFS_POST_AWARD);

        return findCompetitionById(competitionId)
                .andOnSuccessReturnVoid(competition -> {

                    Optional<GrantProcessConfiguration> config = grantProcessConfigurationRepository.findByCompetitionId(competitionId);

                    if (config.isPresent()) {
                        GrantProcessConfiguration grantProcessConfiguration = config.get();
                        grantProcessConfiguration.setSendByDefault(sendByDefault);
                        grantProcessConfigurationRepository.save(grantProcessConfiguration);
                    } else {
                        GrantProcessConfiguration grantProcessConfiguration = new GrantProcessConfiguration();
                        grantProcessConfiguration.setCompetition(competition);
                        grantProcessConfiguration.setSendByDefault(sendByDefault);
                        grantProcessConfigurationRepository.save(grantProcessConfiguration);
                    }
                });
    }

    @Override
    public ServiceResult<CompetitionPostAwardServiceResource> getPostAwardService(long competitionId) {
        return findCompetitionById(competitionId)
                .andOnSuccessReturn(competition -> {

                    Optional<GrantProcessConfiguration> config = grantProcessConfigurationRepository.findByCompetitionId(competitionId);

                    PostAwardService postAwardService;
                    if (config.isPresent() && config.get().isSendByDefault()) {
                        postAwardService = PostAwardService.IFS_POST_AWARD;
                    } else {
                        postAwardService = PostAwardService.CONNECT;
                    }

                    CompetitionPostAwardServiceResource resource = new CompetitionPostAwardServiceResource();
                    resource.setCompetitionId(competitionId);
                    resource.setPostAwardService(postAwardService);
                    return resource;
                });
    }
}
