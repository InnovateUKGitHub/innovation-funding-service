package org.innovateuk.ifs.application.forms.questions.horizon.model;

import lombok.Getter;
import lombok.Setter;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgrammeResource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Setter
@Getter
public class HorizonWorkProgrammeSelectionData {

    long applicationId;
    HorizonWorkProgrammeResource workProgramme;
    HorizonWorkProgrammeResource callId;

    public HorizonWorkProgrammeSelectionData() {
    }

    public HorizonWorkProgrammeSelectionData(long applicationId) {
        this.applicationId = applicationId;
    }

    public HorizonWorkProgrammeSelectionData(long applicationId, HorizonWorkProgrammeResource workProgramme, HorizonWorkProgrammeResource callId) {
        this.applicationId = applicationId;
        this.workProgramme = workProgramme;
        this.callId = callId;
    }

    public List<HorizonWorkProgrammeResource> getAllSelections() {
        return Stream.of(
                this.workProgramme, this.callId)
                .collect(Collectors.toList());
    }

}
