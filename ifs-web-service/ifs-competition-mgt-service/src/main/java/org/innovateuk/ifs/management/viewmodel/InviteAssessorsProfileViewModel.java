package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.util.List;

/**
 * View model for the Assessor Profile view.
 */
public class InviteAssessorsProfileViewModel {

    private CompetitionResource competition;
    private String name;
    private String email;
    private String phone;
    private AddressResource addressResource;
    private List<CategoryResource> innovationAreas;
    private String businessType;
    private String skills;

    public InviteAssessorsProfileViewModel(
            CompetitionResource competition,
            String name,
            String email,
            String phone,
            AddressResource addressResource,
            List<CategoryResource> innovationAreas,
            String businessType,
            String skills
    ) {
        this.competition = competition;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.addressResource = addressResource;
        this.innovationAreas = innovationAreas;
        this.businessType = businessType;
        this.skills = skills;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public AddressResource getAddressResource() {
        return addressResource;
    }

    public List<CategoryResource> getInnovationAreas() {
        return innovationAreas;
    }

    public String getBusinessType() {
        return businessType;
    }

    public String getSkills() {
        return skills;
    }
}
