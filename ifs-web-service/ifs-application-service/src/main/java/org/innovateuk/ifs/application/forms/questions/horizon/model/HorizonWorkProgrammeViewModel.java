package org.innovateuk.ifs.application.forms.questions.horizon.model;

import org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class HorizonWorkProgrammeViewModel {

    private final String applicationName;
    private final Long applicationId;
    private final boolean complete;
    private final boolean open;
    private final boolean leadApplicant;
    private final Map<String, List<HorizonWorkProgramme>> readOnlyMap;
    private final Set<HorizonWorkProgramme> workProgrammes;
    private final boolean readOnly;
    private String pageTitle;

    public HorizonWorkProgrammeViewModel(String applicationName,
                                          Long applicationId,
                                          String pageTitle,
                                          boolean complete,
                                          boolean open,
                                          boolean leadApplicant,
                                          Set<HorizonWorkProgramme> workProgrammes,
                                          boolean readOnly,
                                          Map<String, List<HorizonWorkProgramme>> readOnlyMap
    ) {
        this.applicationName = applicationName;
        this.applicationId = applicationId;
        this.pageTitle = pageTitle;
        this.complete = complete;
        this.open = open;
        this.leadApplicant = leadApplicant;
        this.workProgrammes = workProgrammes;
        this.readOnly = readOnly;
        this.readOnlyMap = readOnlyMap;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getPageTitle() {
        return pageTitle;
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

    public Set<HorizonWorkProgramme> getWorkProgrammes() {
        return workProgrammes;
    }

    public Map<String, List<HorizonWorkProgramme>> getReadOnlyMap() {
        return readOnlyMap;
    }

    public boolean isReadOnly() {
        return readOnly;
    }
}
