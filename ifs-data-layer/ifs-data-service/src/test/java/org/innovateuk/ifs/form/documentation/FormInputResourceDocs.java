package org.innovateuk.ifs.form.documentation;

import com.google.common.collect.ImmutableSet;
import org.innovateuk.ifs.form.builder.FormInputResourceBuilder;

import static org.innovateuk.ifs.file.resource.FileTypeCategory.PDF;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.SPREADSHEET;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.resource.FormInputType.TEXTAREA;

public class FormInputResourceDocs {

    public static final FormInputResourceBuilder formInputResourceBuilder = newFormInputResource()
            .withId(1L)
            .withType(TEXTAREA)
            .withWordCount(140)
            .withAllowedFileTypes(ImmutableSet.of(SPREADSHEET, PDF));
}
