package com.worth.ifs.assessment.model.profile;

import com.worth.ifs.assessment.viewmodel.profile.AssessorProfileSkillsViewModel;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Assessor Skills view.
 */
@Component
public class AssessorProfileSkillsModelPopulator {
    public AssessorProfileSkillsViewModel populateModel() {
        String expertise = "High Value Manufacturing";
        return new AssessorProfileSkillsViewModel(expertise);
    }
}
