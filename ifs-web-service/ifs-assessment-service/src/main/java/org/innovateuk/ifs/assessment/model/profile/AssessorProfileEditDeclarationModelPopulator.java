package org.innovateuk.ifs.assessment.model.profile;

import org.innovateuk.ifs.assessment.viewmodel.profile.AssessorProfileEditDeclarationViewModel;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

import static java.time.Month.MARCH;

/**
 * Build the model for the Assessor Declaration Edit view.
 */
@Component
public class AssessorProfileEditDeclarationModelPopulator {

    private Clock clock = Clock.systemDefaultZone();

    public AssessorProfileEditDeclarationViewModel populateModel() {
        LocalDate declarationDate = calculateDeclarationDate();
        return new AssessorProfileEditDeclarationViewModel(declarationDate);
    }

    private LocalDate calculateDeclarationDate() {
        LocalDate now = LocalDate.now(clock);
        LocalDate financialYearEndDayInCurrentYear = LocalDate.of(now.getYear(), MARCH, 31);

        // Has the financial year end day already been reached during this year?
        boolean yearEndPassed = now.compareTo(financialYearEndDayInCurrentYear) >= 0;

        return yearEndPassed ? financialYearEndDayInCurrentYear.plusYears(1) : financialYearEndDayInCurrentYear;
    }
}
