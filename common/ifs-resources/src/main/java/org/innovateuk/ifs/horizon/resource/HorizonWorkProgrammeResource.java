package org.innovateuk.ifs.horizon.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class HorizonWorkProgrammeResource implements Comparable<HorizonWorkProgrammeResource> {

    private long id;
    private String name;
    private HorizonWorkProgrammeResource parentWorkProgramme;
    private boolean enabled;

    public HorizonWorkProgrammeResource() {}

    public HorizonWorkProgrammeResource(long  id, String name, boolean enabled) {
        this.id = id;
        this.name = name;
        this.enabled = enabled;
    }

    public HorizonWorkProgrammeResource(long id, String name, HorizonWorkProgrammeResource parentWorkProgramme, boolean enabled) {
        this.id = id;
        this.name = name;
        this.parentWorkProgramme = parentWorkProgramme;
        this.enabled = enabled;
    }

    @JsonIgnore
    public boolean isWorkProgramme() {
        return parentWorkProgramme == null || parentWorkProgramme.getId() <= 0;
    }

    @JsonIgnore
    public boolean isCallerId() {
        return parentWorkProgramme != null && parentWorkProgramme.getId() > 0;
    }

    @JsonIgnore
    public String getWorkProgrammeName() {
       return isCallerId() ? getParentWorkProgramme().getName() : name;
    }

    @Override
    public int compareTo(HorizonWorkProgrammeResource that) {
        return that == null ? -1 : Long.compare(this.getId(), that.getId());
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof HorizonWorkProgrammeResource && ((HorizonWorkProgrammeResource) that).getId() == this.getId();
    }

}
