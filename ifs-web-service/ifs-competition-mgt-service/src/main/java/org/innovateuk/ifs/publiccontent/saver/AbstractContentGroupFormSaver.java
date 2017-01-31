package org.innovateuk.ifs.publiccontent.saver;


import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentGroupResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.publiccontent.form.AbstractContentGroupForm;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Saver for the SearchInformationForm form.
 */
@Service
public abstract class AbstractContentGroupFormSaver<F extends AbstractContentGroupForm> extends AbstractPublicContentFormSaver<F> {

    @Override
    protected List<Error> populateResource(F form, PublicContentResource publicContentResource) {
        Optional<PublicContentSectionResource> optional = CollectionFunctions.simpleFindFirst(publicContentResource.getContentSections(),
                contentSectionResource -> getType().equals(contentSectionResource.getType()));
        if (optional.isPresent()) {
            if(!canHaveEmptyGroups() && form.getContentGroups().isEmpty()) {
                //TODO replace with validation property.
                return Collections.singletonList(Error.fieldError("contentGroups", form.getContentGroups(),
                        "validation.publiccontent.contentgroup.notempty"));
            }
            optional.get().setContentGroups(form.getContentGroups().stream().map(contentGroupForm -> {
                ContentGroupResource contentGroupResource = new ContentGroupResource();
                contentGroupResource.setId(contentGroupForm.getId());
                contentGroupResource.setHeading(contentGroupForm.getHeading());
                contentGroupResource.setContent(contentGroupForm.getContent());
                contentGroupResource.setPriority(form.getContentGroups().indexOf(contentGroupForm));
                return contentGroupResource;
            }).collect(Collectors.toList()));
            return Collections.emptyList();
        }

        return Collections.singletonList(Error.globalError(CommonFailureKeys.PUBLIC_CONTENT_NOT_INITIALISED));
    }

    protected abstract boolean canHaveEmptyGroups();

}
