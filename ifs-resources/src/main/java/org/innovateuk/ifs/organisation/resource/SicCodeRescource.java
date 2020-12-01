package org.innovateuk.ifs.organisation.resource;

import java.util.Objects;

public class SicCodeRescource {

    private Long id;
    private Long organisation;
    private String sicCode;

    public SicCodeRescource()
    {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Long organisation) {
        this.organisation = organisation;
    }

    public String getSicCode() {
        return sicCode;
    }

    public SicCodeRescource(String sicCode){
        this.sicCode = sicCode;
    }

    public void setSicCode(String sicCode) {
        this.sicCode = sicCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SicCodeRescource that = (SicCodeRescource) o;
        return id.equals(that.id) &&
                organisation.equals(that.organisation) &&
                sicCode.equals(that.sicCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, organisation, sicCode);
    }
}
