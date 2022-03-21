package org.innovateuk.ifs.application.forms.questions.heukar.model;

import org.innovateuk.ifs.heukar.resource.HeukarLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HeukarProjectLocationSelectionData {

    long applicationId;
    List<HeukarLocation> parentSelections = new ArrayList<>();
    List<HeukarLocation> englandSelections = new ArrayList<>();
    List<HeukarLocation> overseasSelections = new ArrayList<>();
    List<HeukarLocation> crownDependencySelections = new ArrayList<>();

    public HeukarProjectLocationSelectionData() {

    }

    public HeukarProjectLocationSelectionData(long applicationId) {
        this.applicationId = applicationId;
    }

    public HeukarProjectLocationSelectionData(long applicationId,
                                              List<HeukarLocation> parentSelections,
                                              List<HeukarLocation> englandSelections,
                                              List<HeukarLocation> overseasSelections,
                                              List<HeukarLocation> crownDependencySelections) {
        this.applicationId = applicationId;
        this.parentSelections = parentSelections;
        this.englandSelections = englandSelections;
        this.overseasSelections = overseasSelections;
        this.crownDependencySelections = crownDependencySelections;
    }

    public List<HeukarLocation> getAllSelections() {
        return Stream.of(this.parentSelections,
                        this.englandSelections,
                        this.overseasSelections,
                        this.crownDependencySelections)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public List<HeukarLocation> getParentSelections() {
        return parentSelections;
    }

    public void setParentSelections(List<HeukarLocation> parentSelections) {
        this.parentSelections = parentSelections;
    }

    public List<HeukarLocation> getEnglandSelections() {
        return englandSelections;
    }

    public void setEnglandSelections(List<HeukarLocation> englandSelections) {
        this.englandSelections = englandSelections;
    }

    public List<HeukarLocation> getOverseasSelections() {
        return overseasSelections;
    }

    public void setOverseasSelections(List<HeukarLocation> overseasSelections) {
        this.overseasSelections = overseasSelections;
    }

    public List<HeukarLocation> getCrownDependencySelections() {
        return crownDependencySelections;
    }

    public void setCrownDependencySelections(List<HeukarLocation> crownDependencySelections) {
        this.crownDependencySelections = crownDependencySelections;
    }
}
