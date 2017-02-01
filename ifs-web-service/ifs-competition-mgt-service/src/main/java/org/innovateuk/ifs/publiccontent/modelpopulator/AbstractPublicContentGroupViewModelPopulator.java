package org.innovateuk.ifs.publiccontent.modelpopulator;


import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentGroupResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.publiccontent.viewmodel.AbstractPublicContentGroupViewModel;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * Abstract class to populate the generic fields needed in the view.
 * @param <M> the view model class.
 */
public abstract class AbstractPublicContentGroupViewModelPopulator<M extends AbstractPublicContentGroupViewModel> extends AbstractPublicContentViewModelPopulator<M> implements PublicContentViewModelPopulator<M> {

    @Autowired
    private CompetitionService competitionService;

    protected void populateSection(M model, PublicContentResource publicContentResource) {
        Optional<PublicContentSectionResource> optionalSection = publicContentResource.getContentSections().stream().filter(filterSection -> getType().equals(filterSection.getType())).findAny();
        if (!optionalSection.isPresent()) {
            model.setFileEntries(CollectionFunctions.simpleToMap(optionalSection.get().getContentGroups(),
                    ContentGroupResource::getId, ContentGroupResource::getFileEntry));
        }
    }
}
