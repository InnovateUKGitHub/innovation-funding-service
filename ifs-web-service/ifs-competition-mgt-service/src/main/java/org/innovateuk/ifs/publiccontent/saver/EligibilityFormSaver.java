package org.innovateuk.ifs.publiccontent.saver;


import org.innovateuk.ifs.competition.publiccontent.resource.ContentGroupResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.EligibilityForm;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Saver for the SearchInformationForm form.
 */
@Service
public class EligibilityFormSaver extends AbstractPublicContentFormSaver<EligibilityForm> implements PublicContentFormSaver<EligibilityForm> {

    @Override
    protected void populateResource(EligibilityForm form, PublicContentResource publicContentResource) {
        Optional<PublicContentSectionResource> optional = CollectionFunctions.simpleFindFirst(publicContentResource.getContentSections(),
                contentSectionResource -> getType().equals(contentSectionResource.getType()));
        if (optional.isPresent()) {
            optional.get().setContentGroups(form.getContentGroups().stream().map(contentGroupForm -> {
                ContentGroupResource contentGroupResource = new ContentGroupResource();
                contentGroupResource.setId(contentGroupForm.getId());
                contentGroupResource.setHeading(contentGroupForm.getHeading());
                contentGroupResource.setContent(contentGroupForm.getContent());
                contentGroupResource.setPriority(form.getContentGroups().indexOf(contentGroupForm));
                return contentGroupResource;
            }).collect(Collectors.toList()));
        }
    }


    @Override
    protected PublicContentSectionType getType() {
        return PublicContentSectionType.ELIGIBILITY;
    }
}
