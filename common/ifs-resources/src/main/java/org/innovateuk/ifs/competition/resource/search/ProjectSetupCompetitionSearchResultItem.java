package org.innovateuk.ifs.competition.resource.search;

import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.time.ZonedDateTime;
import java.util.Set;

public class ProjectSetupCompetitionSearchResultItem extends AbstractCompetitionSearchResultItem {

    private Integer projectsCount;
    private ZonedDateTime manageDecisionEmailDate;

    private ProjectSetupCompetitionSearchResultItem() {} //for jackson

    public ProjectSetupCompetitionSearchResultItem(long id, String name, CompetitionStatus competitionStatus, String competitionTypeName, Set<String> innovationAreaNames, Integer projectsCount, ZonedDateTime manageDecisionEmailDate) {
        super(id, name, competitionStatus, competitionTypeName, innovationAreaNames);
        this.projectsCount = projectsCount;
        this.manageDecisionEmailDate = manageDecisionEmailDate;
    }

    public Integer getProjectsCount() {
        return projectsCount;
    }

    public ZonedDateTime getManageDecisionEmailDate() {
        return manageDecisionEmailDate;
    }
}
