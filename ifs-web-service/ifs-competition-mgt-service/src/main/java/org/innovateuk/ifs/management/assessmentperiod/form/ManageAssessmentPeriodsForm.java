package org.innovateuk.ifs.management.assessmentperiod.form;

import org.apache.commons.collections4.map.LinkedMap;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import java.util.*;
import java.util.stream.Collectors;

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

    public void orderMilestoneEnties(){
        getAssessmentPeriods().forEach(p -> p.setMilestoneEntries(
                p.getMilestoneEntries()
                        .entrySet()
                        .stream()
                        .sorted(Comparator.comparing(entry -> MilestoneType.valueOf(entry.getKey()).ordinal()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedMap::new))
        ));
    }
}

