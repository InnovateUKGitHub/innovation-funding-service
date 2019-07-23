package org.innovateuk.ifs.management.publiccontent.formpopulator;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.management.publiccontent.form.AbstractContentGroupForm;
import org.innovateuk.ifs.management.publiccontent.form.ContentGroupForm;
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
            if (!getType().isAllowEmptyContentGroups() && optionalSection.get().getContentGroups().isEmpty())  {
                return Collections.singletonList(new ContentGroupForm());
            }
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
