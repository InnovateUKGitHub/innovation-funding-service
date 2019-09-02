package org.innovateuk.ifs.project.core.domain;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;

import javax.persistence.*;

/**
 * Entity representing a group of project setup stages
 */
@Entity
public class ProjectStages {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competitionId", referencedColumnName = "id")
    public Competition competition;

    @Enumerated(EnumType.STRING)
    private ProjectSetupStage projectSetupStage;

    private Long priority;

    public ProjectStages() {
    }

    public ProjectStages(Competition competition, ProjectSetupStage projectSetupStage, Long priority) {
        this.competition = competition;
        this.projectSetupStage = projectSetupStage;
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public ProjectSetupStage getProjectSetupStage() {
        return projectSetupStage;
    }

    public void setProjectSetupStage(ProjectSetupStage projectSetupStage) {
        this.projectSetupStage = projectSetupStage;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }
}
