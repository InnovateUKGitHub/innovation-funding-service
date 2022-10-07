package org.innovateuk.ifs.management.assessment.viewmodel;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.innovateuk.ifs.competition.resource.AvailableAssessorsSortFieldType;

/**
 * Holder of model attributes for the Application Progress view.
 */
@EqualsAndHashCode
@AllArgsConstructor
@Getter
public class ApplicationAssessmentProgressUnsubmitViewModel {

    private long competitionId;
    private long applicationId;
    private long assessmentId;
    private long assessmentPeriodId;
    private AvailableAssessorsSortFieldType sortField;
}