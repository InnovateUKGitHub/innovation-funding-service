package org.innovateuk.ifs.application.domain;

import javax.persistence.*;

@Entity
@Table(name = "application_pre_reg_config")
public class ApplicationPreRegConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean enableForEOI;

    @OneToOne(mappedBy = "applicationPreRegConfig", fetch = FetchType.LAZY)
    private Application application;

    public ApplicationPreRegConfig(Long id, boolean enableForEOI, Application application) {
        this.id = id;
        this.enableForEOI = enableForEOI;
        this.application = application;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEnableForEOI() {
        return enableForEOI;
    }

    public void setEnableForEOI(boolean enableForEOI) {
        this.enableForEOI = enableForEOI;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }
}