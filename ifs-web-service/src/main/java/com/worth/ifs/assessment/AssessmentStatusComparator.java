package com.worth.ifs.assessment;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.AssessmentStates;

public class AssessmentStatusComparator implements java.util.Comparator<Assessment> {
    @Override
    public int compare(Assessment assessment1, Assessment assessment2) {
        int result = Integer.compare(AssessmentStates.getByState(assessment1.getProcessStatus()).getOrdinal(),
                AssessmentStates.getByState(assessment2.getProcessStatus()).getOrdinal());

        if(result == 0) {
            return assessment1.getId().compareTo(assessment2.getId());
        }
        return result;
    }
}
