package org.innovateuk.ifs.competition.populator;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Component that determines the PublicContentStatusText for a PublicContentItem.
 */
@Component
public class PublicContentStatusDeterminer {
    public PublicContentStatusText getApplicablePublicContentStatusText(PublicContentItemResource publicContentItemResource) {
        return Arrays.stream(PublicContentStatusText.values())
                .filter(indicator -> indicator.getPredicate().test(publicContentItemResource))
                .findFirst().get();
    }
}
