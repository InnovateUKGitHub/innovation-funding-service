package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentGroupResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.singletonList;

/**
 * Generates data from Competition Funders and attaches it to a competition
 */
public class PublicContentGroupDataBuilder extends BaseDataBuilder<Void, PublicContentGroupDataBuilder>{

    public PublicContentGroupDataBuilder withPublicContentGroup(String competitionName, String heading, String content, PublicContentSectionType type) {
        return asCompAdmin(data -> {
            if (competitionName != null) {
                Competition competition = retrieveCompetitionByName(competitionName);
                PublicContentResource publicContent = publicContentService.findByCompetitionId(competition.getId()).getSuccessObjectOrThrowException();
                PublicContentSectionResource section = publicContent.getContentSections().stream().filter(filterSection -> type.equals(filterSection.getType())).findAny().get();

                ContentGroupResource contentGroup = new ContentGroupResource();
                contentGroup.setContent(content);
                contentGroup.setSectionType(type);
                contentGroup.setHeading(heading);
                contentGroup.setPriority(0);
                section.setContentGroups(singletonList(contentGroup));

                publicContentService.updateSection(publicContent, section.getType());
            }
        });
    }

    public static PublicContentGroupDataBuilder newPublicContentGroupDataBuilder(ServiceLocator serviceLocator) {
        return new PublicContentGroupDataBuilder(Collections.emptyList(), serviceLocator);
    }

    private PublicContentGroupDataBuilder(List<BiConsumer<Integer, Void>> multiActions,
                                          ServiceLocator serviceLocator) {

        super(multiActions, serviceLocator);
    }

    @Override
    protected PublicContentGroupDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Void>> actions) {
        return new PublicContentGroupDataBuilder(actions, serviceLocator);
    }

    @Override
    protected Void createInitial() {
        return null;
    }
}
