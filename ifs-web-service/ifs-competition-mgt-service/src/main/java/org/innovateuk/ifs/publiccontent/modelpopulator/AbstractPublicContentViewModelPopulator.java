package org.innovateuk.ifs.publiccontent.modelpopulator;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.publiccontent.viewmodel.AbstractPublicContentViewModel;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract class to populate the generic fields needed in the view.
 * @param <M> the view model class.
 */
public abstract class AbstractPublicContentViewModelPopulator<M extends AbstractPublicContentViewModel> implements PublicContentViewModelPopulator<M> {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Override
    public M populate(PublicContentResource publicContentResource, boolean readOnly) {
        M model = createInitial();
        model.setCompetition(competitionRestService.getCompetitionById(publicContentResource.getCompetitionId()).getSuccess());
        model.setPublished(publicContentResource.getPublishDate() != null);
        model.setReadOnly(readOnly);

        publicContentResource.getContentSections().stream()
                .filter(section -> getType().equals(section.getType()))
                .findAny()
                .ifPresent(publicContentSectionResource -> {
                    model.setSection(publicContentSectionResource);
                    populateSection(model, publicContentResource, publicContentSectionResource);
                });

        return model;
    }

    protected abstract M createInitial();
    protected abstract void populateSection(M model, PublicContentResource publicContentResource, PublicContentSectionResource section);
    protected abstract PublicContentSectionType getType();
}
