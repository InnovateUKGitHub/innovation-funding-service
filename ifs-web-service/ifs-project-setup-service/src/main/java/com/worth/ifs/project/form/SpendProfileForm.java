package com.worth.ifs.project.form;

import com.worth.ifs.controller.BaseBindingResultTarget;
import com.worth.ifs.project.resource.SpendProfileTableResource;

public class SpendProfileForm  extends BaseBindingResultTarget {

    private SpendProfileTableResource table;

    // for spring form binding
    public SpendProfileForm() {

    }

    public SpendProfileTableResource getTable() {
        return table;
    }

    public void setTable(SpendProfileTableResource table) {
        this.table = table;
    }

}