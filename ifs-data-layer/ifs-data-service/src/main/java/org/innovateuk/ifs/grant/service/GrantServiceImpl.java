package org.innovateuk.ifs.grant.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.sil.grant.service.GrantEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class GrantServiceImpl implements GrantService {
    private static final Log LOG = LogFactory.getLog(GrantServiceImpl.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private GrantEndpoint grantEndpoint;

    @Override
    @Transactional
    public ServiceResult<Void> sendProject(Long applicationId) {
        LOG.info("Sending project : " + applicationId);
        Project project = projectRepository.findOneByApplicationId(applicationId);
        Grant grant = new Grant();
        grant.setId(project.getId());
        grantEndpoint.send(grant);
        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> sendReadyProjects() {
        List<Project> readyProjects = projectRepository.findReadyToSend();
        LOG.info("Sending " + readyProjects.size() + " projects");
        readyProjects.forEach(it -> sendProject(it.getApplication().getId()));
        return serviceSuccess();
    }
}
