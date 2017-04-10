package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.publiccontent.domain.ContentEvent;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Generates data from Competition Funders and attaches it to a competition
 */
public class PublicContentDateDataBuilder extends BaseDataBuilder<Void, PublicContentDateDataBuilder>{

    public PublicContentDateDataBuilder withPublicContentDate(String competitionName, ZonedDateTime date, String content) {
        return with(data -> {
            if (competitionName != null) {
                Competition competition = retrieveCompetitionByName(competitionName);
                PublicContent publicContent = publicContentRepository.findByCompetitionId(competition.getId());

                ContentEvent event = new ContentEvent();
                event.setDate(date);
                event.setContent(content);
                event.setPublicContent(publicContent);
                contentEventRepository.save(event);
            }
        });
    }

    public static PublicContentDateDataBuilder newPublicContentDateDataBuilder(ServiceLocator serviceLocator) {
        return new PublicContentDateDataBuilder(Collections.emptyList(), serviceLocator);
    }

    private PublicContentDateDataBuilder(List<BiConsumer<Integer, Void>> multiActions,
                                         ServiceLocator serviceLocator) {

        super(multiActions, serviceLocator);
    }

    @Override
    protected PublicContentDateDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Void>> actions) {
        return new PublicContentDateDataBuilder(actions, serviceLocator);
    }

    @Override
    protected Void createInitial() {
        return null;
    }
}
