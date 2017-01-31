package org.innovateuk.ifs.publiccontent.formpopulator;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.publiccontent.form.AbstractContentGroupForm;
import org.innovateuk.ifs.publiccontent.form.ContentGroupForm;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public abstract class AbstractContentGroupFormPopulator<F extends AbstractContentGroupForm> extends AbstractPublicContentFormPopulator<F> implements PublicContentFormPopulator<F> {

    @Override
    protected void populateSection(F form, PublicContentResource publicContentResource) {
        form.setContentGroups(getContentGroups(publicContentResource));
    }

    private List<ContentGroupForm> getContentGroups(PublicContentResource publicContentResource) {
        Optional<PublicContentSectionResource> optionalSection = publicContentResource.getContentSections().stream().filter(filterSection -> getType().equals(filterSection.getType())).findAny();
        if (!optionalSection.isPresent()) {
            return Collections.emptyList();
        } else {
            return optionalSection.get().getContentGroups().stream().map(contentGroupResource -> {
                ContentGroupForm contentGroupForm = new ContentGroupForm();
                contentGroupForm.setId(contentGroupResource.getId());
                contentGroupForm.setHeading(contentGroupResource.getHeading());
                contentGroupForm.setContent(contentGroupResource.getContent());
                return contentGroupForm;
            }).collect(Collectors.toList());
        }
    }

}
