package org.innovateuk.ifs.management.competition.setup.postawardservice.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.resource.PostAwardService;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.NotNull;

public class PostAwardServiceForm extends BaseBindingResultTarget {

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private PostAwardService postAwardService;

    public PostAwardService getPostAwardService() {
        return postAwardService;
    }

    public void setPostAwardService(PostAwardService postAwardService) {
        this.postAwardService = postAwardService;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PostAwardServiceForm that = (PostAwardServiceForm) o;

        return new EqualsBuilder()
                .append(postAwardService, that.postAwardService)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(postAwardService)
                .toHashCode();
    }
}
