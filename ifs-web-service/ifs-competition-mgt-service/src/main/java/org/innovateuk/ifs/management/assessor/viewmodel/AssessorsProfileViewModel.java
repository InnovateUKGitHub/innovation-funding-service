package org.innovateuk.ifs.management.assessor.viewmodel;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.competition.viewmodel.InnovationSectorViewModel;

import java.util.List;

/**
 * View model for the Assessor Profile view.
 */
public class AssessorsProfileViewModel {

    private CompetitionResource competition;
    private String name;
    private String email;
    private String phone;
    private AddressResource address;
    private List<InnovationSectorViewModel> innovationSectors;
    private String businessType;
    private String skills;

    public AssessorsProfileViewModel(
            CompetitionResource competition,
            String name,
            String email,
            String phone,
            AddressResource addressResource,
            List<InnovationSectorViewModel> innovationSectors,
            String businessType,
            String skills) {
        this.competition = competition;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = addressResource;
        this.innovationSectors = innovationSectors;
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

    public AddressResource getAddress() {
        return address;
    }

    public List<InnovationSectorViewModel> getInnovationSectors() { return innovationSectors; }

    public String getBusinessType() {
        return businessType;
    }

    public String getSkills() {
        return skills;
    }
}
