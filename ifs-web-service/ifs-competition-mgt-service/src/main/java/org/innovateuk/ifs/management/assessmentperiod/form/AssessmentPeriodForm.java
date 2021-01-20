package org.innovateuk.ifs.management.assessmentperiod.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import java.util.ArrayList;
import java.util.List;

public class AssessmentPeriodForm extends BaseBindingResultTarget {

    List<AssessmentPeriodMilestonesForm> formList;

    public List<AssessmentPeriodMilestonesForm> getFormList() {
        return formList;
    }

    public void setFormList(List<AssessmentPeriodMilestonesForm> formList) {
        this.formList = formList;
    }

    public void addNewAssessmentPeriod(AssessmentPeriodMilestonesForm formToAdd) {
        if(formList == null){
            this.formList = new ArrayList<>();
        }
        this.formList.add(formToAdd);
    }

    public void addExistingAssessmentPeriods(List<AssessmentPeriodMilestonesForm> existingAssessmentPeriods) {
        if (this.formList == null) {
            this.formList = new ArrayList<>();
        }

        this.formList.addAll(existingAssessmentPeriods);
    }

}

