package org.innovateuk.ifs.application.forms.questions.heukar.model;

import org.innovateuk.ifs.heukar.resource.HeukarLocation;

import java.util.List;
import java.util.Map;

public class HeukarProjectLocationViewModel {

    private final String applicationName;
    private final Long applicationId;
    private final boolean complete;
    private final boolean open;
    private final boolean leadApplicant;
    private Map<String, List<HeukarLocation>> readOnlyMap;
    private boolean readOnly;
    private final String pageTitle;

    public HeukarProjectLocationViewModel(String applicationName,
                                          Long applicationId,
                                          boolean complete,
                                          boolean open,
                                          boolean leadApplicant,
                                          String pageTitle,
                                          boolean readOnly,
                                          Map<String, List<HeukarLocation>> readOnlyMap
    ) {
        this.applicationName = applicationName;
        this.applicationId = applicationId;
        this.complete = complete;
        this.open = open;
        this.leadApplicant = leadApplicant;
        this.pageTitle = pageTitle;
        this.readOnly = readOnly;
        this.readOnlyMap = readOnlyMap;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isLeadApplicant() {
        return leadApplicant;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public Map<String, List<HeukarLocation>> getReadOnlyMap() {
        return readOnlyMap;
    }

    public void setReadOnlyMap(Map<String, List<HeukarLocation>> readOnlyMap) {
        this.readOnlyMap = readOnlyMap;

    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
