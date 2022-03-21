package org.innovateuk.ifs.heukar.domain;
import org.innovateuk.ifs.heukar.resource.HeukarLocation;

import javax.persistence.*;

@Entity
@Table(name = "application_heukar_location")
public class ApplicationHeukarLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long applicationId;

    @Enumerated(EnumType.STRING)
    private HeukarLocation location;

    public ApplicationHeukarLocation() {
    }

    public ApplicationHeukarLocation(Long applicationId, HeukarLocation location) {
        this.applicationId = applicationId;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public HeukarLocation getLocation() {
        return location;
    }

    public void setLocation(HeukarLocation location) {
        this.location = location;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }
}
