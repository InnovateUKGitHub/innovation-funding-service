package org.innovateuk.ifs.nonifs.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;

import java.util.List;

/**
 * View model for the Non-IFS competition details page.
 */
public class NonIfsDetailsViewModel {

    private List<InnovationSectorResource> innovationSectors;
    private List<InnovationAreaResource> innovationAreas;

    public List<InnovationSectorResource> getInnovationSectors() {
        return innovationSectors;
    }

    public void setInnovationSectors(List<InnovationSectorResource> innovationSectors) {
        this.innovationSectors = innovationSectors;
    }

    public List<InnovationAreaResource> getInnovationAreas() {
        return innovationAreas;
    }

    public void setInnovationAreas(List<InnovationAreaResource> innovationAreas) {
        this.innovationAreas = innovationAreas;
    }
}
