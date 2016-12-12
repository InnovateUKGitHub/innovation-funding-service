package org.innovateuk.ifs.login.model;

import org.innovateuk.ifs.login.viewmodel.RoleSelectionViewModel;
import org.springframework.stereotype.Component;

/**
 * Build the model for role selection view.
 */
@Component
public class RoleSelectionModelPopulator {

    public RoleSelectionViewModel populateModel() {
        return new RoleSelectionViewModel();
    }
}
