package org.innovateuk.ifs.publiccontent.modelpopulator;


import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSection;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.publiccontent.viewmodel.AbstractPublicContentViewModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public abstract class AbstractPublicContentViewModelPopulator<M extends AbstractPublicContentViewModel> implements PublicContentViewModelPopulator<M> {

    @Autowired
    private CompetitionService competitionService;

    @Override
    public M populate(PublicContentResource publicContentResource, boolean readOnly) {
        M model = createInitial();
        model.setCompetition(competitionService.getById(publicContentResource.getCompetitionId()));
        Optional<PublicContentSectionResource> optionalSectionResource = publicContentResource.getContentSections().stream()
                .filter(section -> getType().equals(section.getType())).findAny();
        if (optionalSectionResource.isPresent()) {
            model.setSection(optionalSectionResource.get());
        }
        model.setPublished(publicContentResource.getPublishDate() != null);
        model.setReadOnly(readOnly);
        populateSection(model, publicContentResource);
        return model;
    }

    protected abstract M createInitial();
    protected abstract void populateSection(M model, PublicContentResource publicContentResource);
    protected abstract PublicContentSection getType();
}
