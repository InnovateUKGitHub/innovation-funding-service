package org.innovateuk.ifs.application.forms.questions.horizon.model;

import org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public HorizonWorkProgramme getWorkProgramme() {
        return workProgramme;
    }

    public void setWorkProgramme(HorizonWorkProgramme workProgramme) {
        this.workProgramme = workProgramme;
    }

    public HorizonWorkProgramme getCallId() {
        return callId;
    }

    public void setCallId(HorizonWorkProgramme callId) {
        this.callId = callId;
    }
}
