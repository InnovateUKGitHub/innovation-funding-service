package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.transactional.BaseTransactionalService;

import java.util.Optional;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * Abstract service for handling project service functionality.
 */
public class AbstractProjectServiceImpl extends BaseTransactionalService {

    protected Optional<ProjectUser> getFinanceContact(final Project project, final Organisation organisation) {
        return simpleFindFirst(project.getProjectUsers(), pu -> pu.getRole().isFinanceContact()
                && pu.getOrganisation().getId().equals(organisation.getId()));
    }
}
