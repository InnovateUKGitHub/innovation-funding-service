package org.innovateuk.ifs.login.model;

import org.innovateuk.ifs.login.viewmodel.RoleSelectionViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

/**
 * Build the model for role selection view.
 */
@Component
public class RoleSelectionModelPopulator {

    public RoleSelectionViewModel populateModel(UserResource user) {
        return new RoleSelectionViewModel(user);
    }
}
