package org.innovateuk.ifs.management.decision.form;

import javax.validation.constraints.Min;

/**
 * Contains the Funding Decision pagination values.
 */

public class DecisionPaginationForm {

    @Min(value = 0, message = "{validation.applicationsummaryqueryform.page.min}")
    private Integer page = 0;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}