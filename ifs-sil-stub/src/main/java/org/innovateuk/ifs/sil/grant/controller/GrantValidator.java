package org.innovateuk.ifs.sil.grant.controller;

import org.innovateuk.ifs.sil.grant.resource.Grant;

import java.util.ArrayList;
import java.util.List;

class GrantValidator {
    List<String> checkForErrors(Grant grant) {
        List<String> errors = new ArrayList<>();
        if (grant.getParticipants() == null) {
            errors.add("participants is null");
        } else if (grant.getParticipants().isEmpty()) {
            errors.add("participants are empty");
        } else {
            grant.getParticipants().forEach(participant -> {
                if (participant.getForecasts() == null) {
                    errors.add("forecast for participant " + participant.getId() + " is null");
                } else if (participant.getForecasts().isEmpty()) {
                    errors.add("forecast for participant " + participant.getId() + " is empty");
                }
            });
        }
        return errors;
    }
}
