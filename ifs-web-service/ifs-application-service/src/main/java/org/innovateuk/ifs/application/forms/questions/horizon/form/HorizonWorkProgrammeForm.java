package org.innovateuk.ifs.application.forms.questions.horizon.form;

import lombok.Getter;
import lombok.Setter;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgrammeResource;

import javax.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
public class HorizonWorkProgrammeForm {
    private String title;
    private List<HorizonWorkProgrammeResource> allOptions;

    @NotNull(message = "{validation.horizon.programme.required}")
    private Long selected;

    public HorizonWorkProgrammeForm() {
    }

    public HorizonWorkProgrammeForm(String title, List<HorizonWorkProgrammeResource> allOptions, Long selected) {
        this.title = title;
        this.allOptions = allOptions;
        this.selected = selected;
    }
}
