package org.innovateuk.ifs.assessment.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.Valid;
import java.util.List;

import static java.util.Collections.singletonList;

public class AssessorFormInputResponsesResource {

    @Valid
    private List<AssessorFormInputResponseResource> responses;

    public AssessorFormInputResponsesResource() {
    }

    public AssessorFormInputResponsesResource(AssessorFormInputResponseResource response) {
        this.responses = singletonList(response);
    }

    public AssessorFormInputResponsesResource(List<AssessorFormInputResponseResource> responses) {
        this.responses = responses;
    }

    public List<AssessorFormInputResponseResource> getResponses() {
        return responses;
    }

    public void setResponses(final List<AssessorFormInputResponseResource> responses) {
        this.responses = responses;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final AssessorFormInputResponsesResource that = (AssessorFormInputResponsesResource) o;

        return new EqualsBuilder()
                .append(responses, that.responses)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(responses)
                .toHashCode();
    }
}
