package org.innovateuk.ifs.management.assessmentperiod.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import java.util.List;

public class ManageAssessmentPeriodsForm extends BaseBindingResultTarget {

    private List<AssessmentPeriodForm> assessmentPeriods;

    public List<AssessmentPeriodForm> getAssessmentPeriods() {
        return assessmentPeriods;
    }

    public ManageAssessmentPeriodsForm setAssessmentPeriods(List<AssessmentPeriodForm> assessmentPeriods) {
        this.assessmentPeriods = assessmentPeriods;
        return this;
    }
}

