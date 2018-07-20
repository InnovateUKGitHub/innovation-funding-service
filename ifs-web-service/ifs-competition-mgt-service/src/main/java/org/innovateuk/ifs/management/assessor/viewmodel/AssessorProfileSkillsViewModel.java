package org.innovateuk.ifs.management.assessor.viewmodel;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.competition.viewmodel.InnovationSectorViewModel;

import java.util.List;

/**
 * View model for the Assessors skills view.
 */
public class AssessorProfileSkillsViewModel {

    private CompetitionResource competition;
    private long assessorId;
    private String name;
    private String email;
    private String phone;
    private AddressResource address;
    private List<InnovationSectorViewModel> innovationSectors;
    private String businessType;
    private String skills;
    private String originQuery;

    public AssessorProfileSkillsViewModel(
            CompetitionResource competition,
            long assessorId,
            String name,
            String email,
            String phone,
            AddressResource addressResource,
            List<InnovationSectorViewModel> innovationSectors,
            String businessType,
            String skills,
            String originQuery) {
        this.competition = competition;
        this.assessorId = assessorId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = addressResource;
        this.innovationSectors = innovationSectors;
        this.businessType = businessType;
        this.skills = skills;
        this.originQuery = originQuery;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public long getAssessorId() {
        return assessorId;
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

    public String getOriginQuery() {
        return originQuery;
    }
}
