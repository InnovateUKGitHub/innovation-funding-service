package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.grant.domain.GrantProcess;
import org.innovateuk.ifs.grant.domain.GrantProcessConfiguration;
import org.innovateuk.ifs.grant.repository.GrantProcessConfigurationRepository;
import org.innovateuk.ifs.grant.repository.GrantProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GrantProcessServiceImpl implements GrantProcessService {
    @Autowired
    private GrantProcessRepository grantProcessRepository;

    @Autowired
    private GrantProcessConfigurationRepository grantProcessConfigurationRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public void createGrantProcess(long applicationId) {
        Optional<Application> application = applicationRepository.findById(applicationId);
        application.ifPresent((a) -> {
            Optional<GrantProcessConfiguration> config = grantProcessConfigurationRepository.findByCompetitionId(a.getCompetition().getId());
            boolean sendByDefault = config.map(GrantProcessConfiguration::isSendByDefault).orElse(false);
            grantProcessRepository.save(new GrantProcess(applicationId, sendByDefault));
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<GrantProcess> findReadyToSend() {
        return grantProcessRepository.findByPendingIsTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GrantProcess> findOneReadyToSend() {
        return grantProcessRepository.findFirstByPendingIsTrue();
    }

    @Override
    public void sendSucceeded(long applicationId) {
        GrantProcess process = grantProcessRepository.findOneByApplicationId(applicationId);
        grantProcessRepository.save(process.sendSucceeded(ZonedDateTime.now()));
    }

    @Override
    public void sendFailed(long applicationId, String message) {
        GrantProcess process = grantProcessRepository.findOneByApplicationId(applicationId);
        grantProcessRepository.save(process.sendFailed(ZonedDateTime.now(), message));
    }
}
