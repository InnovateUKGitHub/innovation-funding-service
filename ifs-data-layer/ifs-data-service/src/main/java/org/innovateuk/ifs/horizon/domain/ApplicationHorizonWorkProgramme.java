package org.innovateuk.ifs.horizon.domain;

import org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme;

import javax.persistence.*;

@Entity
@Table(name = "application_horizon_work_programme")
public class ApplicationHorizonWorkProgramme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long applicationId;

    @Enumerated(EnumType.STRING)
    private HorizonWorkProgramme workProgramme;

    public ApplicationHorizonWorkProgramme() {
    }

    public ApplicationHorizonWorkProgramme(Long id, Long applicationId, HorizonWorkProgramme workProgramme) {
        this.id = id;
        this.applicationId = applicationId;
        this.workProgramme = workProgramme;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public HorizonWorkProgramme getWorkProgramme() {
        return workProgramme;
    }

    public void setWorkProgramme(HorizonWorkProgramme workProgramme) {
        this.workProgramme = workProgramme;
    }
}
