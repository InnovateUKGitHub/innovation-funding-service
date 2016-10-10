package com.worth.ifs.assessment.model.profile;

import com.worth.ifs.assessment.viewmodel.profile.AssessorProfileDeclarationViewModel;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

/**
 * Build the model for the Assessor Declaration view.
 */
@Component
public class AssessorProfileDeclarationModelPopulator {

    public AssessorProfileDeclarationViewModel populateModel() {
        // TODO
        LocalDate nextFinancialYearEnd = LocalDate.now();
        return new AssessorProfileDeclarationViewModel(nextFinancialYearEnd);
    }
}