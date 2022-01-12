package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.common.viewmodel.ApplicationSubsidyBasisPartnerRowViewModel;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationSubsidyBasisViewModel;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ApplicationSubsidyBasisViewModelTest {

    @Test
    public void test(){
        List<ApplicationSubsidyBasisPartnerRowViewModel> notCompleted = asList(
                new ApplicationSubsidyBasisPartnerRowViewModel("lead organisation", true, true, true, 1, 1, 1),
                new ApplicationSubsidyBasisPartnerRowViewModel("collaborator organisation", true, null, false, 1, 1, 1));

        // The collaborator has not completed the subsidy basis question
        assertFalse(new ApplicationSubsidyBasisViewModel(notCompleted).isSubsidyBasisCompletedByAllOrganisations());

        List<ApplicationSubsidyBasisPartnerRowViewModel> completed = asList(
                new ApplicationSubsidyBasisPartnerRowViewModel("lead organisation", true, true, true, 1, 1, 1),
                new ApplicationSubsidyBasisPartnerRowViewModel("collaborator organisation", true, true, true, 1, 1, 1));

        // Both the lead and collaborator have completed the subsidy basis question
        assertTrue(new ApplicationSubsidyBasisViewModel(completed).isSubsidyBasisCompletedByAllOrganisations());
    }
}
