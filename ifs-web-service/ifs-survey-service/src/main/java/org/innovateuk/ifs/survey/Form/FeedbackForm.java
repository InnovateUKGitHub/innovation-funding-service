package org.innovateuk.ifs.survey.Form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.commons.validation.constraints.WordCount;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.survey.Satisfaction;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;


/**
 * Form field model for the Satisfaction survey
 */
public class FeedbackForm extends BaseBindingResultTarget {

    @NotBlank(message = "{validation.standard.satisfaction.selectionrequired}")
    private String satisfaction;

    @NotBlank(message = "{validation.standard.comments.required}")
    @Size(max = 5000, message = "{validation.field.too.many.characters}")
    @WordCount(max = 400, message = "{validation.field.max.word.count}")
    private String comments;

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(String satisfaction) {
        this.satisfaction = satisfaction;
    }

    public List<Satisfaction> getSatisfactionList() {
        List<Satisfaction> satisfactionList  = new ArrayList<Satisfaction>();
        for(Satisfaction s : Satisfaction.values()){
            satisfactionList.add(s);
        }

        return satisfactionList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FeedbackForm that = (FeedbackForm) o;

        return new EqualsBuilder()
                .append(satisfaction, that.satisfaction)
                .append(comments, that.comments)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(satisfaction)
                .append(comments)
                .toHashCode();
    }
}