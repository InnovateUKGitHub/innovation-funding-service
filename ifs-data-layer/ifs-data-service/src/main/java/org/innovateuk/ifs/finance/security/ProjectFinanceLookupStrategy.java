package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.finance.mapper.ProjectFinanceMapper;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResourceId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class ProjectFinanceLookupStrategy {
    @Autowired
    ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    ProjectFinanceMapper projectMapper;

    @PermissionEntityLookupStrategy
    public ProjectFinanceResource getProjectFinance(final ProjectFinanceResourceId id) {
        final ProjectFinanceResource projectFinanceResource = projectMapper.mapToResource(projectFinanceRepository.findByProjectIdAndOrganisationId(id.getProjectId(), id.getOrganisationId()));
        // If its new then this could be empty so fill in the fields we can
        projectFinanceResource.setProject(id.getProjectId());
        projectFinanceResource.setOrganisation(id.getOrganisationId());
        return  projectFinanceResource;
    }


    @PermissionEntityLookupStrategy
    public ProjectFinanceResource getProjectFinance(final Long id) {
        return projectMapper.mapToResource(projectFinanceRepository.findOne(id));
    }
}
