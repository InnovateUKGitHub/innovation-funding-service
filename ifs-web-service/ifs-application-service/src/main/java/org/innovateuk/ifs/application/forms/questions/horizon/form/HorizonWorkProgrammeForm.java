package org.innovateuk.ifs.application.forms.questions.horizon.form;

import lombok.Getter;
import lombok.Setter;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme;

import javax.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
public class HorizonWorkProgrammeForm {
    private String title;
    private List<HorizonWorkProgramme> allOptions;

    @NotNull(message = "{validation.horizon.programme.required}")
    private HorizonWorkProgramme selected;

    public HorizonWorkProgrammeForm() {
    }

    public HorizonWorkProgrammeForm(String title, List<HorizonWorkProgramme> allOptions, HorizonWorkProgramme selected) {
        this.title = title;
        this.allOptions = allOptions;
        this.selected = selected;
    }
}
