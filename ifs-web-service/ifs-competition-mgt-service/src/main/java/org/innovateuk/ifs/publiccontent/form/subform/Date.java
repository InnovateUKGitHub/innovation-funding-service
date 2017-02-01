package org.innovateuk.ifs.publiccontent.form.subform;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * The repeating date that is being used in the {@link org.innovateuk.ifs.publiccontent.form.DatesForm}
 */
public class Date {
    @NotEmpty
    private Integer day;
    @NotEmpty
    private Integer month;
    @NotEmpty
    private Integer year;
    @NotEmpty
    private String content;
}
