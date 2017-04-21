package org.innovateuk.ifs.project.bankdetails.populator;

import org.innovateuk.ifs.form.AddressForm;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.bankdetails.form.ChangeBankDetailsForm;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.viewmodel.BankDetailsReviewViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.getOnlyElement;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
/**
 * Populator for creating the {@link BankDetailsReviewViewModel}
 */

@Service
public class BankDetailsReviewModelPopulator {

    @Autowired
    private ProjectService projectService;

    public BankDetailsReviewViewModel populateBankDetailsReviewViewModel(OrganisationResource organisation, ProjectResource project, BankDetailsResource bankDetails){
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(project.getId());
        ProjectUserResource financeContact = getOnlyElement(simpleFilter(projectUsers, pr -> pr.isFinanceContact() && organisation.getId().equals(pr.getOrganisation())));
        return buildViewModel(project, financeContact, organisation, bankDetails);
    }

    private BankDetailsReviewViewModel buildViewModel(ProjectResource project, ProjectUserResource financeContact, OrganisationResource organisation, BankDetailsResource bankDetails){
        return new BankDetailsReviewViewModel(
                project.getId(),
                project.getApplication(),
                project.getName(),
                financeContact.getUserName(),
                financeContact.getEmail(),
                financeContact.getPhoneNumber(),
                organisation.getId(),
                organisation.getName(),
                organisation.getCompanyHouseNumber(),
                bankDetails.getAccountNumber(),
                bankDetails.getSortCode(),
                bankDetails.getOrganisationAddress().getAddress().getAsSingleLine(),
                bankDetails.isVerified(),
                bankDetails.getCompanyNameScore(),
                bankDetails.getRegistrationNumberMatched(),
                bankDetails.getAddressScore(),
                bankDetails.isApproved(),
                bankDetails.isManualApproval());
    }

    public void populateExitingBankDetailsInForm(OrganisationResource organisation, BankDetailsResource bankDetails, ChangeBankDetailsForm form){
        form.setOrganisationName(organisation.getName());
        form.setRegistrationNumber(organisation.getCompanyHouseNumber());
        form.setSortCode(bankDetails.getSortCode());
        form.setAccountNumber(bankDetails.getAccountNumber());
        populateAddress(form.getAddressForm(), bankDetails);
    }

    private void populateAddress(AddressForm addressForm, BankDetailsResource bankDetails){
        addressForm.setManualAddress(true);
        addressForm.setSelectedPostcode(bankDetails.getOrganisationAddress().getAddress());
    }

}