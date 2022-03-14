package org.innovateuk.ifs.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.util.List;
import java.util.Map;

public class AssessorProfileSkillsViewModel {

    private CompetitionResource competition;
    private AssessorProfileDetailsViewModel assessorProfileDetailsViewModel;
    private Map<String, List<String>> innovationAreas;
    private String skillAreas;
    private boolean compAdminUser;
    private final boolean isEDIUpdateEnabled;


    private String ediUpdateUrl;
    public AssessorProfileSkillsViewModel(CompetitionResource competition,
                                          AssessorProfileDetailsViewModel assessorProfileDetailsViewModel,
                                          Map<String, List<String>> innovationAreas,
                                          String skillAreas,
                                          boolean compAdminUser,
                                          boolean isEDIUpdateEnabled,
                                          String ediUpdateUrl) {
        this.competition = competition;
        this.assessorProfileDetailsViewModel = assessorProfileDetailsViewModel;
        this.innovationAreas = innovationAreas;
        this.skillAreas = skillAreas;
        this.compAdminUser = compAdminUser;
        this.isEDIUpdateEnabled = isEDIUpdateEnabled;
        this.ediUpdateUrl = ediUpdateUrl;
    }

    public CompetitionResource getCompetition() {
        return competition;
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

    public boolean isCompAdminUser() {
        return compAdminUser;
    }

    public boolean isEDIUpdateEnabled() {
        return isEDIUpdateEnabled;
    }
    public String getEdiUpdateUrl() {
        return ediUpdateUrl;
    }

}
