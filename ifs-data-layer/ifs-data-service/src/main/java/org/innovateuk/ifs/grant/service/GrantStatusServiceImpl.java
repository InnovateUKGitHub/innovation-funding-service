package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.grant.domain.GrantStatus;
import org.innovateuk.ifs.grant.repository.GrantStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class GrantStatusServiceImpl implements GrantStatusService {
    @Autowired
    private GrantStatusRepository grantStatusRepository;

    @Override
    public List<GrantStatus> findReadyToSend() {
        return grantStatusRepository.findReadyToSend();
    }

    @Override
    public void sendRequested(long applicationId) {
        GrantStatus status = new GrantStatus();
        status.setSentRequested(ZonedDateTime.now());
        status.setApplicationId(applicationId);
        grantStatusRepository.save(status);
    }

    @Override
    public void sendSucceeded(long applicationId) {
        GrantStatus status = grantStatusRepository.findOneByApplicationId(applicationId);
        status.setSentSucceeded(ZonedDateTime.now());
        grantStatusRepository.save(status);
    }
}
