package org.innovateuk.ifs.management.assessmentperiod.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import java.util.ArrayList;
import java.util.List;

public class ManageAssessmentPeriodsForm extends BaseBindingResultTarget {

    private List<AssessmentPeriodForm> assessmentPeriods = new ArrayList<>();

    public List<AssessmentPeriodForm> getAssessmentPeriods() {
        return assessmentPeriods;
    }

    public void setAssessmentPeriods(List<AssessmentPeriodForm> assessmentPeriods) {
        this.assessmentPeriods = assessmentPeriods;
    }
}

