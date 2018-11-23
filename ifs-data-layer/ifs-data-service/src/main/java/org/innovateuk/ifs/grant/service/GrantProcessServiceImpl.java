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
        return grantProcessRepository.findReadyToSend();
    }

    @Override
    public void sendRequested(long applicationId) {
        GrantProcess process = new GrantProcess();
        process.setSentRequested(ZonedDateTime.now());
        process.setApplicationId(applicationId);
        grantProcessRepository.save(process);
    }

    @Override
    public void sendSucceeded(long applicationId) {
        GrantProcess process = grantProcessRepository.findOneByApplicationId(applicationId);
        process.setSentSucceeded(ZonedDateTime.now());
        grantProcessRepository.save(process);
    }
}
