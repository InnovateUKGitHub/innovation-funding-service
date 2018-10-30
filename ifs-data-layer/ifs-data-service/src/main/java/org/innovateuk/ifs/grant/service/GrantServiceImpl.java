package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.sil.grant.service.GrantEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class GrantServiceImpl implements GrantService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private GrantEndpoint grantEndpoint;

    @Override
    public ServiceResult<Void> sendProject(Long applicationId) {
        Project project = projectRepository.findOneByApplicationId(applicationId);
        Grant grant = new Grant();
        grant.setId(project.getId());
        grantEndpoint.send(grant);
        return serviceSuccess();
    }
}
