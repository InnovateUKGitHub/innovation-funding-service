package org.innovateuk.ifs.horizon.domain;

import javax.persistence.*;

@Entity
@Table(name = "horizon_work_programme")
public class HorizonWorkProgramme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private HorizonWorkProgramme parentWorkProgramme;

    private boolean enabled;

    public HorizonWorkProgramme() {
    }

    public HorizonWorkProgramme(Long id, String name, HorizonWorkProgramme workProgramme, boolean enabled) {
        this.id = id;
        this.name = name;
        this.parentWorkProgramme = workProgramme;
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HorizonWorkProgramme getParentWorkProgramme() {
        return parentWorkProgramme;
    }

    public void setParentWorkProgramme(HorizonWorkProgramme parentWorkProgramme) { this.parentWorkProgramme = parentWorkProgramme; }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
