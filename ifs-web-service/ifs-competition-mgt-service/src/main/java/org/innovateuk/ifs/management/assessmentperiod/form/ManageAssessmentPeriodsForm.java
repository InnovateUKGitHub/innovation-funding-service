package org.innovateuk.ifs.management.assessmentperiod.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import java.util.ArrayList;
import java.util.List;

public class ManageAssessmentPeriodsForm extends BaseBindingResultTarget {

    List<AssessmentPeriodForm> formList;

    public List<AssessmentPeriodForm> getFormList() {
        return formList;
    }

    public void setFormList(List<AssessmentPeriodForm> formList) {
        this.formList = formList;
    }

    public void addNewAssessmentPeriod(AssessmentPeriodForm formToAdd) {
        if(formList == null){
            this.formList = new ArrayList<>();
        }
        this.formList.add(formToAdd);
    }

    public void addExistingAssessmentPeriods(List<AssessmentPeriodForm> existingAssessmentPeriods) {
        if (this.formList == null) {
            this.formList = new ArrayList<>();
        }

        this.formList.addAll(existingAssessmentPeriods);
    }

}

