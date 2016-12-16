package org.innovateuk.ifs.management.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class InviteNewAssessorsForm extends BaseBindingResultTarget {

    @Valid
    private List<NewAssessorInviteForm> invites = new ArrayList<>();

    @NotNull(message = "{validation.inviteNewAssessorsForm.selectedInnovationArea.required}")
    private Long selectedInnovationArea;

    public List<NewAssessorInviteForm> getInvites() {
        return invites;
    }

    public void setInvites(List<NewAssessorInviteForm> invites) {
        this.invites = invites;
    }

    public Long getSelectedInnovationArea() {
        return selectedInnovationArea;
    }

    public void setSelectedInnovationArea(Long selectedInnovationArea) {
        this.selectedInnovationArea = selectedInnovationArea;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InviteNewAssessorsForm that = (InviteNewAssessorsForm) o;

        return new EqualsBuilder()
                .append(selectedInnovationArea, that.selectedInnovationArea)
                .append(invites, that.invites)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(invites)
                .append(selectedInnovationArea)
                .toHashCode();
    }

    public static class NewAssessorInviteForm {

        @NotEmpty(message = "{validation.standard.name.required}")
        private String name;

        @NotEmpty(message = "{validation.standard.email.required}")
        private String email;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            NewAssessorInviteForm that = (NewAssessorInviteForm) o;

            return new EqualsBuilder()
                    .append(name, that.name)
                    .append(email, that.email)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(name)
                    .append(email)
                    .toHashCode();
        }
    }
}
