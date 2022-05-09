package org.innovateuk.ifs.management.publiccontent.formpopulator;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.publiccontent.form.AbstractPublicContentForm;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract class for populating a public content form section.
 * @param <F> the form class.
 */
public abstract class AbstractPublicContentFormPopulator<F extends AbstractPublicContentForm> implements PublicContentFormPopulator<F> {

    @Autowired
    private CompetitionRestService competitionRestService;
    public F populate(PublicContentResource publicContentResource) {
        F form = createInitial();
        populateSection(form, publicContentResource);
        return form;
    }

    public CompetitionResource getCompetition(long competitionId) {
        return competitionRestService.getCompetitionById(competitionId).getSuccess();
    }

    protected abstract F createInitial();
    protected abstract void populateSection(F form, PublicContentResource publicContentResource);
    protected abstract PublicContentSectionType getType();
}
