package com.worth.ifs.project.viewmodel;

import com.worth.ifs.commons.rest.LocalDateResource;
import com.worth.ifs.project.resource.ProjectResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.validation.ObjectError;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * TODO
 */
public class TotalSpendProfileViewModel {
    private ProjectResource project;
    private TotalProjectSpendTableViewModel table;
    private SpendProfileSummaryModel summary;

    public TotalSpendProfileViewModel(ProjectResource project, TotalProjectSpendTableViewModel table, SpendProfileSummaryModel summary) {
        this.project = project;
        this.table = table;
        this.summary = summary;
    }

    public ProjectResource getProject() {
        return project;
    }
    public TotalProjectSpendTableViewModel getTable() {
        return table;
    }
    public SpendProfileSummaryModel getSummary() { return summary; }
}

