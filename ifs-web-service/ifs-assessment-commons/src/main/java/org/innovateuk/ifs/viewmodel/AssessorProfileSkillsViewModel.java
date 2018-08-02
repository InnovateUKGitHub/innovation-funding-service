package org.innovateuk.ifs.viewmodel;

import java.util.List;
import java.util.Map;

public class AssessorProfileSkillsViewModel {

    private AssessorProfileDetailsViewModel assessorProfileDetailsViewModel;
    private Map<String, List<String>> innovationAreas;
    private String skillAreas;

    public AssessorProfileSkillsViewModel(AssessorProfileDetailsViewModel assessorProfileDetailsViewModel,
                                          Map<String, List<String>> innovationAreas,
                                          String skillAreas) {
        this.assessorProfileDetailsViewModel = assessorProfileDetailsViewModel;
        this.innovationAreas = innovationAreas;
        this.skillAreas = skillAreas;
    }

    public AssessorProfileDetailsViewModel getAssessorProfileDetailsViewModel() {
        return assessorProfileDetailsViewModel;
    }

    public Map<String, List<String>> getInnovationAreas() {
        return innovationAreas;
    }

    public String getSkillAreas() {
        return skillAreas;
    }
}
