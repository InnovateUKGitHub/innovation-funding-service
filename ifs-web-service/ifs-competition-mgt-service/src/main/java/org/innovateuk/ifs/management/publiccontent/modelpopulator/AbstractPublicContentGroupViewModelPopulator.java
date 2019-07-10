package org.innovateuk.ifs.management.publiccontent.modelpopulator;


import org.innovateuk.ifs.competition.publiccontent.resource.ContentGroupResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.management.publiccontent.viewmodel.AbstractPublicContentGroupViewModel;
import org.innovateuk.ifs.util.CollectionFunctions;

/**
 * Abstract class to populate the generic fields needed in the view.
 * @param <M> the view model class.
 */
public abstract class AbstractPublicContentGroupViewModelPopulator<M extends AbstractPublicContentGroupViewModel> extends AbstractPublicContentViewModelPopulator<M> implements PublicContentViewModelPopulator<M> {

    @Override
    protected void populateSection(M model, PublicContentResource publicContentResource, PublicContentSectionResource section) {
            model.setFileEntries(CollectionFunctions.simpleToMap(section.getContentGroups(),
                    ContentGroupResource::getId, ContentGroupResource::getFileEntry));
    }
}
