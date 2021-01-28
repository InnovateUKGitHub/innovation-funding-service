package org.innovateuk.ifs.project.grantofferletter.template.resource;

import org.innovateuk.ifs.competition.resource.VersionedTemplateResource;

/**
 * Resource representation of GolTemplate
 */
public class GolTemplateResource extends VersionedTemplateResource {

    public static final String DEFAULT_GOL_TEMPLATE = "Default GOL Template";

    public GolTemplateResource() {
    }

    public GolTemplateResource(String name, String template, int version) {
        super(name, template, version);
    }
}
