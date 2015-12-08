package com.worth.ifs.file.domain.builders;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.file.domain.FormInputResponseFile;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class FormInputResponseFileBuilder extends BaseBuilder<FormInputResponseFile, FormInputResponseFileBuilder> {

    private FormInputResponseFileBuilder(List<BiConsumer<Integer, FormInputResponseFile>> multiActions) {
        super(multiActions);
    }

    public static FormInputResponseFileBuilder newFormInputResponseFile() {
        return new FormInputResponseFileBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected FormInputResponseFileBuilder createNewBuilderWithActions(List<BiConsumer<Integer, FormInputResponseFile>> actions) {
        return new FormInputResponseFileBuilder(actions);
    }

    @Override
    protected FormInputResponseFile createInitial() {
        return new FormInputResponseFile();
    }

    public FormInputResponseFileBuilder withFormInputResponseId(Long... ids) {
        return withArray((id, file) -> setField("formInputResponseId", id, file), ids);
    }
}