package org.innovateuk.ifs.grant.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.grant.domain.GrantProcess;
import org.innovateuk.ifs.grant.repository.GrantProcessRepository;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.sil.grant.service.GrantEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.groupingBy;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class GrantServiceImpl implements GrantService {
    private static final Log LOG = LogFactory.getLog(GrantServiceImpl.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private GrantProcessService grantProcessService;

    @Autowired
    private GrantEndpoint grantEndpoint;

    @Autowired
    private GrantMapper grantMapper;

    @Autowired
    private GrantProcessApplicationFilter grantProcessApplicationFilter;

    @Override
    @Transactional
    public ServiceResult<Void> sendProject(Long applicationId) {
        LOG.info("Sending project : " + applicationId);

        Grant grant = grantMapper.mapToGrant(
                projectRepository.findOneByApplicationId(applicationId)
        );
        if (grantProcessApplicationFilter.shouldSend(grant)) {
            grantEndpoint.send(grant)
                    .andOnSuccess(() -> grantProcessService.sendSucceeded(applicationId))
                    .andOnFailure((ServiceFailure serviceFailure) ->
                            grantProcessService.sendFailed(applicationId, serviceFailure.toDisplayString()));
        } else {
            grantProcessService.sendIgnored(applicationId, grantProcessApplicationFilter.generateFilterReason(grant));
        }
        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> sendReadyProjects() {
        List<GrantProcess> readyToSend = grantProcessService.findReadyToSend();
        LOG.info("Sending " + readyToSend.size() + " projects");
        readyToSend.forEach(it -> sendProject(it.getApplicationId()));
        return serviceSuccess();
    }
}
