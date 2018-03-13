package org.innovateuk.ifs.workflow.repository;

import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;

import javax.persistence.Converter;

@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class ApplicationStateConverter extends IdentifiableEnumConverter<ApplicationState> {

    public ApplicationStateConverter() {
        super(ApplicationState.class);
    }

}