package org.innovateuk.ifs.management.assessmentperiod.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ManageAssessmentPeriodsForm extends BaseBindingResultTarget {

    private List<AssessmentPeriodForm> assessmentPeriods = new ArrayList<>();

    public List<AssessmentPeriodForm> getAssessmentPeriods() {
        return assessmentPeriods;
    }

    public void setAssessmentPeriods(List<AssessmentPeriodForm> assessmentPeriods) {
        this.assessmentPeriods = assessmentPeriods;
    }

    public long numberUnsavedAssessmentPeriods() {
        return assessmentPeriods.stream().map(AssessmentPeriodForm::getAssessmentPeriodId).filter(Objects::isNull).count();
    }
}

