package org.innovateuk.ifs.competition.resource;

import lombok.Getter;
import lombok.Setter;

/**
 * Key stats to be displayed in the competitions funded panel for eoi
 */
@Getter
@Setter
public class CompetitionEoiKeyApplicationStatisticsResource {

    private int EOISubmitted;
    private int EOISuccessful;
    private int EOIUnsuccessful;
    private int EOINotifiedOfDecision;
    private int EOIAwaitingDecision;

}
