package org.innovateuk.ifs.project.form.validation;

import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Enable extra validations if an invite is needed
 */
public class ValidateInviteSequenceProvider implements DefaultGroupSequenceProvider<ValidateInviteForm>{
    public List<Class<?>> getValidationGroups(ValidateInviteForm form) {
        List<Class<?>> defaultGroupSequence = new ArrayList<Class<?>>();
        defaultGroupSequence.add(ValidateInviteForm.class);
        if(form != null && form.inviteRequired()) {
            defaultGroupSequence.add(ValidateInviteChecks.class);
        }
        return defaultGroupSequence;
    }
}
