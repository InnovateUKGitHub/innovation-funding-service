package org.innovateuk.ifs.application.common.viewmodel;


public class ApplicationSubsidyBasisViewModel {

    private final boolean subsidyBasisCompletedByAllOrganisations;

    public ApplicationSubsidyBasisViewModel(boolean subsidyBasisCompletedByAllOrganisations) {
        this.subsidyBasisCompletedByAllOrganisations = subsidyBasisCompletedByAllOrganisations;
    }

    public boolean isSubsidyBasisCompletedByAllOrganisations() {
        return subsidyBasisCompletedByAllOrganisations;
    }
}