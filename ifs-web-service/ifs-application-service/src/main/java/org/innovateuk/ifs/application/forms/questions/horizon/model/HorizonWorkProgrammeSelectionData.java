package org.innovateuk.ifs.application.forms.questions.horizon.model;

import lombok.Getter;
import lombok.Setter;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Setter
@Getter
public class HorizonWorkProgrammeSelectionData {

    long applicationId;
    HorizonWorkProgramme workProgramme;
    HorizonWorkProgramme callId;

    public HorizonWorkProgrammeSelectionData() {
    }

    public HorizonWorkProgrammeSelectionData(long applicationId) {
        this.applicationId = applicationId;
    }

    public HorizonWorkProgrammeSelectionData(long applicationId, HorizonWorkProgramme workProgramme, HorizonWorkProgramme callId) {
        this.applicationId = applicationId;
        this.workProgramme = workProgramme;
        this.callId = callId;
    }

    public List<HorizonWorkProgramme> getAllSelections() {
        return Stream.of(
                this.workProgramme, this.callId)
                .collect(Collectors.toList());
    }

}
