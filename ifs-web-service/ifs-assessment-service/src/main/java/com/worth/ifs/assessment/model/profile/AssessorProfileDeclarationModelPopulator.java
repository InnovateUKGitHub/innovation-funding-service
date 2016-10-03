package com.worth.ifs.assessment.model.profile;

import com.worth.ifs.assessment.viewmodel.profile.AssessorProfileDeclarationViewModel;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessor Declaration view.
 */
@Component
public class AssessorProfileDeclarationModelPopulator {

    public AssessorProfileDeclarationViewModel populateModel() {
        return new AssessorProfileDeclarationViewModel();
    }
}