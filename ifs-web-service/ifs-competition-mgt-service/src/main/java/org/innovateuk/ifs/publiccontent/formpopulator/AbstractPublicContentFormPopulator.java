package org.innovateuk.ifs.publiccontent.formpopulator;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.AbstractPublicContentForm;
import org.innovateuk.ifs.publiccontent.form.ContentGroupForm;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Abstract class for populating a public content form section.
 * @param <F> the form class.
 */
public abstract class AbstractPublicContentFormPopulator<F extends AbstractPublicContentForm> implements PublicContentFormPopulator<F> {

    public F populate(PublicContentResource publicContentResource) {
        F form = createInitial();
        populateSection(form, publicContentResource);
        return form;
    }

    protected List<ContentGroupForm> getContentGroupForms(PublicContentResource publicContentResource) {
        Optional<PublicContentSectionResource> optionalSection = publicContentResource.getContentSections().stream().filter(filterSection -> getType().equals(filterSection.getType())).findAny();
        if (!optionalSection.isPresent()) {
            return Collections.emptyList();
        } else {
            return optionalSection.get().getContentGroups().stream().map(contentGroupResource -> {
                ContentGroupForm form = new ContentGroupForm();
                form.setId(contentGroupResource.getId());
                form.setHeading(contentGroupResource.getHeading());
                form.setContent(contentGroupResource.getContent());
                return form;
            }).collect(Collectors.toList());
        }
    }

    protected abstract F createInitial();
    protected abstract void populateSection(F form, PublicContentResource publicContentResource);
    protected abstract PublicContentSectionType getType();
}
