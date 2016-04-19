package com.worth.ifs.application.resource;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
/**Composite ID consisting of a questionID and an applicationId. for doing permission lookups based on the contained ids where both id's are needed*/
public class QuestionApplicationCompositeId implements Serializable {
    public final Long questionId;
    public final Long applicationId;

    public QuestionApplicationCompositeId(Long questionId, Long applicationId){
        this.questionId = questionId;
        this.applicationId = applicationId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        QuestionApplicationCompositeId rhs = (QuestionApplicationCompositeId) obj;
        return new EqualsBuilder()
            .append(this.questionId, rhs.questionId)
            .append(this.applicationId, rhs.applicationId)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(questionId)
            .append(applicationId)
            .toHashCode();
    }

}
