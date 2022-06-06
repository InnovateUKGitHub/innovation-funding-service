package org.innovateuk.ifs.application.resource;

public class ApplicationPreRegConfigResource {

    private Long id;

    private boolean enableForEOI;

    public ApplicationPreRegConfigResource() {
    }

    public ApplicationPreRegConfigResource(Long id, boolean enableForEOI) {
        this.id = id;
        this.enableForEOI = enableForEOI;
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
}
