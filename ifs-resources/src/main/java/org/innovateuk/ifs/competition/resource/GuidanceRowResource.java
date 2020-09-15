package org.innovateuk.ifs.competition.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import javax.validation.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.io.Serializable;

public class GuidanceRowResource implements Serializable {
    public interface GuidanceRowGroup { }

    private Long id;

    @NotBlank(message = "{validation.field.must.not.be.blank}", groups = GuidanceRowResource.GuidanceRowGroup.class)
    @Size(max=255, message = "{validation.applicationquestionform.subject.max}", groups = GuidanceRowResource.GuidanceRowGroup.class)
    private String subject;

    @NotBlank(message = "{validation.field.must.not.be.blank}", groups = GuidanceRowResource.GuidanceRowGroup.class)
    @Size(max=5000, message = "{validation.applicationquestionform.justification.max}", groups = GuidanceRowResource.GuidanceRowGroup.class)
    private String justification;

    private Integer priority;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GuidanceRowResource that = (GuidanceRowResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(subject, that.subject)
                .append(justification, that.justification)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(subject)
                .append(justification)
                .toHashCode();
    }
}
