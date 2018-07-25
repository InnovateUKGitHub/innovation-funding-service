package org.innovateuk.ifs.application.finance.viewmodel;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;

import java.util.Map;

/**
 * Viewmodel for application finance overview
 */
public class ApplicationFinanceOverviewViewModel extends BaseFinanceOverviewViewModel {
    private Map<Long, Pair<BaseFinanceResource, FileEntryResource>> academicFileEntries;
    private Double researchParticipationPercentage;

    public Map<Long, Pair<BaseFinanceResource, FileEntryResource>> getAcademicFileEntries() {
        return academicFileEntries;
    }

    public void setAcademicFileEntries(Map<Long, Pair<BaseFinanceResource, FileEntryResource>> academicFileEntries) {
        this.academicFileEntries = academicFileEntries;
    }

    public Double getResearchParticipationPercentage() {
        return researchParticipationPercentage;
    }

    public void setResearchParticipationPercentage(Double researchParticipationPercentage) {
        this.researchParticipationPercentage = researchParticipationPercentage;
    }

    @Override
    public Boolean getHasAcademicFileEntries() {
        return null != academicFileEntries;
    }

    @Override
    public Boolean hasTooHighResearchRatio(Double maxRatio) {
        return (null != researchParticipationPercentage) && (researchParticipationPercentage > maxRatio);
    }
}
