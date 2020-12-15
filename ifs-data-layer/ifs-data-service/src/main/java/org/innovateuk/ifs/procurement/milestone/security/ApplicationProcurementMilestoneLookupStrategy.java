package org.innovateuk.ifs.procurement.milestone.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.procurement.milestone.mapper.ApplicationProcurementMilestoneMapper;
import org.innovateuk.ifs.procurement.milestone.repository.ApplicationProcurementMilestoneRepository;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneId;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link ApplicationInviteResource}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class ApplicationProcurementMilestoneLookupStrategy {

    @Autowired
    private ApplicationProcurementMilestoneRepository applicationProcurementMilestoneRepository;

    @Autowired
    private ApplicationProcurementMilestoneMapper applicationProcurementMilestoneMapper;

    @PermissionEntityLookupStrategy
    public ProcurementMilestoneResource get(final ApplicationProcurementMilestoneId id) {
        return applicationProcurementMilestoneMapper.mapToResource(applicationProcurementMilestoneRepository.findById(id.getId()).orElse(null));
    }
}
