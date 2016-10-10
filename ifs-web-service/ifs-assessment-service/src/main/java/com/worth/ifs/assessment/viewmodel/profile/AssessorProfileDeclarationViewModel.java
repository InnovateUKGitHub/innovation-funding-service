package com.worth.ifs.assessment.viewmodel.profile;

import java.time.LocalDate;

/**
 * Holder of model attributes for the Declaration of Interests view.
 */
public class AssessorProfileDeclarationViewModel {

    private LocalDate declarationDate;

    public AssessorProfileDeclarationViewModel(LocalDate declarationDate) {
        this.declarationDate = declarationDate;
    }

    public LocalDate getDeclarationDate() {
        return declarationDate;
    }

    public void setDeclarationDate(LocalDate declarationDate) {
        this.declarationDate = declarationDate;
    }
}