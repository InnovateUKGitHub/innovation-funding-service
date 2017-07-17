package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
* Lookup strategies for {@link FinanceRow} and {@link FinanceRowItem} for permissioning
*/
@Component
@PermissionEntityLookupStrategies
public class ProjectFinanceRowLookupStrategy {
   @Autowired
   private ProjectFinanceRowRepository projectFinanceRowRepository;

   @PermissionEntityLookupStrategy
   public ProjectFinanceRow getProjectFinanceRow(final Long costId) {
       return projectFinanceRowRepository.findOne(costId);
   }
}
