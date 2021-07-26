package org.innovateuk.ifs.application.domain;

import javax.persistence.*;

@Entity
@Table(name = "application_external_config")
public class ApplicationExternalConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "applicationExternalConfig",fetch = FetchType.LAZY)
    private Application application;

    private String externalApplicationId;

    private String externalApplicantName;

    public ApplicationExternalConfig() {

    }

    public ApplicationExternalConfig(Application application, String externalApplicationId, String externalApplicantName) {
        this.application = application;
        this.externalApplicationId = externalApplicationId;
        this.externalApplicantName = externalApplicantName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public String getExternalApplicationId() {
        return externalApplicationId;
    }

    public void setExternalApplicationId(String externalApplicationId) {
        this.externalApplicationId = externalApplicationId;
    }

    public String getExternalApplicantName() {
        return externalApplicantName;
    }

    public void setExternalApplicantName(String externalApplicantName) {
        this.externalApplicantName = externalApplicantName;
    }
}
