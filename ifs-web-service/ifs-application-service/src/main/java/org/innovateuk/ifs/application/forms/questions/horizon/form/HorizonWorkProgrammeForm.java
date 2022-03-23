package org.innovateuk.ifs.application.forms.questions.horizon.form;

import org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme;

import java.util.List;

public class HorizonWorkProgrammeForm {
    private String title;
    private List<HorizonWorkProgramme> allOptions;

    private HorizonWorkProgramme selected;

    public HorizonWorkProgrammeForm() {
    }

    public HorizonWorkProgrammeForm(String title, List<HorizonWorkProgramme> allOptions, HorizonWorkProgramme selected) {
        this.title = title;
        this.allOptions = allOptions;
        this.selected = selected;
    }

    public List<HorizonWorkProgramme> getAllOptions() {
        return allOptions;
    }

    public void setAllOptions(List<HorizonWorkProgramme> allOptions) {
        this.allOptions = allOptions;
    }

    public String getTitle() {
        return title;
    }

    public HorizonWorkProgramme getSelected() {
        return selected;
    }

    public void setSelected(HorizonWorkProgramme selected) {
        this.selected = selected;
    }
}
