package org.innovateuk.ifs.project.organisationdetails.view.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel.YourOrganisationDetailsReadOnlyViewModel;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.util.ArrayList;
import java.util.List;

public class OrganisationDetailsViewModel {

    private final long projectId;
    private final long competitionId;
    private final String projectName;
    private final String organisationName;

    private final String organisationType;
    private final String registrationNumber;
    private final String addressLine1;
    private final String addressLine2;
    private final String addressLine3;
    private final String town;
    private final String county;
    private final String postcode;
    private final boolean knowledgeBase;
    private final boolean ktpCompetition;
    private YourOrganisationDetailsReadOnlyViewModel orgDetailsViewModel;
    private boolean partnerOrgDisplay;

    public OrganisationDetailsViewModel(ProjectResource project,
                                        long competitionId,
                                        OrganisationResource organisation,
                                        AddressResource address,
                                        boolean hasPartners,
                                        boolean ktpCompetition) {
        this.projectId = project.getId();
        this.competitionId = competitionId;
        this.projectName = project.getName();
        this.organisationName = organisation.getName();
        this.organisationType = organisation.getOrganisationTypeName();
        this.registrationNumber = organisation.getCompaniesHouseNumber();

        this.addressLine1 = address.getAddressLine1();
        this.addressLine2 = address.getAddressLine2();
        this.addressLine3 = address.getAddressLine3();
        this.town = address.getTown();
        this.county = address.getCounty();
        this.postcode = address.getPostcode();
        this.knowledgeBase = isKnowledgeBase(organisation);
        this.ktpCompetition = ktpCompetition;
    }

    public long getProjectId() {
        return projectId;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public String getOrganisationType() {
        return organisationType;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public String getTown() {
        return town;
    }

    public String getCounty() {
        return county;
    }

    public String getPostcode() {
        return postcode;
    }

    public boolean isKnowledgeBase() {
        return knowledgeBase;
    }

    public boolean isKtpCompetition() {
        return ktpCompetition;
    }

    private boolean isKnowledgeBase(OrganisationResource organisation) {
        return organisation.getOrganisationTypeEnum().equals(OrganisationTypeEnum.KNOWLEDGE_BASE);
    }

    public YourOrganisationDetailsReadOnlyViewModel getOrgDetailsViewModel() {
        return orgDetailsViewModel;
    }

    public void setOrgDetailsViewModel(YourOrganisationDetailsReadOnlyViewModel orgDetailsViewModel) {
        this.orgDetailsViewModel = orgDetailsViewModel;
    }

    public boolean isPartnerOrgDisplay() {
        return partnerOrgDisplay;
    }

    public void setPartnerOrgDisplay(boolean partnerOrgDisplay) {
        partnerOrgDisplay = partnerOrgDisplay;
    }

    @JsonIgnore
    public List<String> getRegisteredAddressString() {
        List<String> addressData = new ArrayList<String>();
        addressData.add(getAddressLine1());
        addressData.add(getTown());
        addressData.add(getCounty());
        addressData.add(getPostcode());
        return addressData;
    }
}