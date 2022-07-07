package org.innovateuk.ifs.horizon.domain;

import javax.persistence.*;

@Entity
@Table(name = "competition_horizon_work_programme")
public class CompetitionHorizonWorkProgramme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long competitionId;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name="work_programme_id", referencedColumnName="id")
    private HorizonWorkProgramme workProgramme;

    public CompetitionHorizonWorkProgramme() {
    }

    public CompetitionHorizonWorkProgramme(Long id, Long competitionId, HorizonWorkProgramme workProgramme) {
        this.id = id;
        this.competitionId = competitionId;
        this.workProgramme = workProgramme;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public HorizonWorkProgramme getWorkProgramme() {
        return workProgramme;
    }

    public void setWorkProgramme(HorizonWorkProgramme workProgramme) {
        this.workProgramme = workProgramme;
    }
}
