package org.innovateuk.ifs.publiccontent.form;

import org.innovateuk.ifs.publiccontent.form.subform.Date;

import javax.validation.Valid;
import java.util.List;

/**
 * Form for the dates page on public content setup.
 */
public class DatesForm extends AbstractPublicContentForm {
    @Valid
    private List<Date> dates;

    public List<Date> getDates() {
        return dates;
    }

    public void setDates(List<Date> dates) {
        this.dates = dates;
    }
}
