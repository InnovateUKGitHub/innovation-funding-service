package com.worth.ifs.assessment.model;

import com.worth.ifs.assessment.viewmodel.AssessorDeclarationViewModel;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessor Declaration view.
 */
@Component
public class AssessorDeclarationModelPopulator {

    public AssessorDeclarationViewModel populateModel() {
        return new AssessorDeclarationViewModel();
    }
}
