package org.innovateuk.ifs.management.application.view.form;

import javax.validation.constraints.Length;
import org.innovateuk.ifs.commons.validation.constraints.WordCount;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.NotBlank;

public class IneligibleApplicationForm extends BaseBindingResultTarget {

    @Length(max = 5000, message = "{validation.field.too.many.characters}")
    @WordCount(max = 400, message = "{validation.field.max.word.count}")
    @NotBlank(message = "{validation.field.must.not.be.blank}")
    private String ineligibleReason;

    public String getIneligibleReason() {
        return ineligibleReason;
    }

    public IneligibleApplicationForm setIneligibleReason(String ineligibleReason) {
        this.ineligibleReason = ineligibleReason;
        return this;
    }
}
