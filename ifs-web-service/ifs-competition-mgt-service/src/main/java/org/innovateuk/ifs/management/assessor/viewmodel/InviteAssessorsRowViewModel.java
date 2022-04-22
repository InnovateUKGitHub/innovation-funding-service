package org.innovateuk.ifs.management.assessor.viewmodel;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;

import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Abstract holder of model attributes for the assessors shown in the 'Invite Assessors' view.
 */
public abstract class InviteAssessorsRowViewModel {

    private Long id;
    private String name;
    private List<InnovationAreaResource> innovationAreas;
    private boolean compliant;
    private boolean validAgreement;
    private boolean validDoi;

    protected InviteAssessorsRowViewModel(Long id,
                                          String name,
                                          List<InnovationAreaResource> innovationAreas,
                                          boolean compliant,
                                          boolean validAgreement,
                                          boolean validDoi) {
        this.id = id;
        this.name = name;
        this.innovationAreas = innovationAreas;
        this.compliant = compliant;
        this.validAgreement = validAgreement;
        this.validDoi = validDoi;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInnovationAreas() {
        return innovationAreas == null ? EMPTY : innovationAreas.stream()
                .map(i -> i.getName())
                .collect(joining(", "));
    }

    public boolean isCompliant() {
        return compliant;
    }

    public boolean isValidAgreement() {
        return validAgreement;
    }

    public boolean isValidDoi() {
        return validDoi;
    }
}