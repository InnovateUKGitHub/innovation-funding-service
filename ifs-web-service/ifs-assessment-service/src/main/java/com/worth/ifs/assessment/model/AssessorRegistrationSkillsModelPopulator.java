package com.worth.ifs.assessment.model;

import com.worth.ifs.assessment.viewmodel.AssessorRegistrationSkillsViewModel;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessor Skills view.
 */
@Component
public class AssessorRegistrationSkillsModelPopulator {
    public AssessorRegistrationSkillsViewModel populateModel() {
        return new AssessorRegistrationSkillsViewModel();
    }
}
