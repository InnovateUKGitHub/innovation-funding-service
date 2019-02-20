package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.grant.domain.GrantProcess;
import org.innovateuk.ifs.grant.repository.GrantProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class GrantProcessServiceImpl implements GrantProcessService {
    @Autowired
    private GrantProcessRepository grantProcessRepository;

    @Override
    public List<GrantProcess> findReadyToSend() {
        return grantProcessRepository.findByPendingIsTrue();
    }

    @Override
    public void sendRequested(long applicationId) {
        GrantProcess process = new GrantProcess(applicationId);
        grantProcessRepository.save(process.requestSend(ZonedDateTime.now()));
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

    @Override
    public void sendIgnored(long applicationId, String message) {
        GrantProcess process = grantProcessRepository.findOneByApplicationId(applicationId);
        grantProcessRepository.save(process.sendIgnored(ZonedDateTime.now(), message));
    }
}