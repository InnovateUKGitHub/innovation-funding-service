package org.innovateuk.ifs.project.form.validation;

import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;
import org.innovateuk.ifs.project.form.ProjectManagerForm;

import java.util.ArrayList;
import java.util.List;

/**
 * Enable extra validations if a project manager will be invited
 */
public class ProjectManagerInviteSequenceProvider implements DefaultGroupSequenceProvider<ProjectManagerForm>{
    public List<Class<?>> getValidationGroups(ProjectManagerForm form) {
        List<Class<?>> defaultGroupSequence = new ArrayList<Class<?>>();
        defaultGroupSequence.add(ProjectManagerForm.class);
        if(form != null && form.getProjectManager() == -1L) {
            defaultGroupSequence.add(ProjectManagerInviteChecks.class);
        }
        return defaultGroupSequence;
    }
}
