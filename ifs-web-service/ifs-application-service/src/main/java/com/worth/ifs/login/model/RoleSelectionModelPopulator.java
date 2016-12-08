package com.worth.ifs.login.model;

import com.worth.ifs.login.viewmodel.RoleSelectionViewModel;
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
