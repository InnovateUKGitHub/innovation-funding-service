package org.innovateuk.ifs.application.forms.questions.heukar.form;

import org.innovateuk.ifs.heukar.resource.HeukarLocation;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class HeukarProjectLocationForm {

    private String title;
    private String guidance;


    private List<HeukarLocation> allOptions;

    @NotEmpty(message = "{validation.projectlocation.required}")
    private List<HeukarLocation> selected;

    public HeukarProjectLocationForm() {
    }

    public void populateForm() {
        this.title = title;
        this.guidance = guidance;
        this.allOptions = allOptions;
        this.selected = selected;
    }

    public List<HeukarLocation> getAllOptions() {
        return allOptions;
    }

    public void setAllOptions(List<HeukarLocation> allOptions) {
        this.allOptions = allOptions;
    }

    public List<HeukarLocation> getSelected() {
        return selected;
    }

    public void setSelected(List<HeukarLocation> selected) {
        this.selected = selected;
    }
}
