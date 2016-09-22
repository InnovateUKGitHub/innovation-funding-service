package com.worth.ifs.assessment.model;

import com.worth.ifs.assessment.viewmodel.AssessorSkillsViewModel;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessor Skills view.
 */
@Component
public class AssessorSkillsModelPopulator {
    public AssessorSkillsViewModel populateModel() {
        return new AssessorSkillsViewModel();
    }
}
