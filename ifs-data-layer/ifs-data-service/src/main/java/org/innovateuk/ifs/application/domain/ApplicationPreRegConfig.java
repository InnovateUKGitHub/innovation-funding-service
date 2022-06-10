package org.innovateuk.ifs.application.domain;

import javax.persistence.*;

@Entity
@Table(name = "application_pre_reg_config")
public class ApplicationPreRegConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="applicationId", referencedColumnName="id")
    private Application application;

    @Column(name = "pre_registration", nullable = false)
    private boolean enableForEOI;

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

    public boolean isEnableForEOI() {
        return enableForEOI;
    }

    public void setEnableForEOI(boolean enableForEOI) {
        this.enableForEOI = enableForEOI;
    }
}