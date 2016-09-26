package com.worth.ifs.assessment.model;

import com.worth.ifs.assessment.viewmodel.AssessorRegistrationDeclarationViewModel;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessor Declaration view.
 */
@Component
public class AssessorRegistrationDeclarationModelPopulator {

    public AssessorRegistrationDeclarationViewModel populateModel() {
        return new AssessorRegistrationDeclarationViewModel();
    }
}
