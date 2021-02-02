package org.innovateuk.ifs.util;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.util.List;

public class MilestoneDateValidationUtils {

   public static ValidationMessages validateCompetitionIdConsistency(List<MilestoneResource> milestones) {
        ValidationMessages vm = new ValidationMessages();
        Long firstMilestoneCompetitionId = milestones.get(0).getCompetitionId();

        boolean allCompetitionIdsMatch = milestones.stream().allMatch(milestone -> milestone.getCompetitionId().equals(firstMilestoneCompetitionId));

        if (!allCompetitionIdsMatch) {
            Error error = new Error("error.title.status.400", HttpStatus.BAD_REQUEST);
            vm.addError(error);
        }

        return vm;
    }

    public static void validatePresetMilestonesSequentialOrder(ValidationMessages vm, List<MilestoneResource> milestones) {
        for (int i = 1; i < milestones.size(); i++) {
            MilestoneResource previous = milestones.get(i - 1);
            MilestoneResource current = milestones.get(i);

            if (current.getDate() != null && previous.getDate() != null && previous.getDate().isAfter(current.getDate())) {
                Error error = new Error("error.milestone.nonsequential", HttpStatus.BAD_REQUEST);
                vm.addError(error);
            }
        }
    }

    public static ValidationMessages validateDateInFuture(List<MilestoneResource> milestones) {
        ValidationMessages vm = new ValidationMessages();

        milestones.forEach(m -> {
            if (m.getDate() != null && m.getDate().isBefore(ZonedDateTime.now())) {
                Error error = new Error("error.milestone.pastdate", HttpStatus.BAD_REQUEST);
                vm.addError(error);
            }
        });

        return vm;
    }

    public static ValidationMessages validateDateNotNull(List<MilestoneResource> milestones) {
        ValidationMessages vm = new ValidationMessages();

        milestones.forEach(m -> {
            if (m.getDate() == null) {
                Error error = new Error("error.milestone.nulldate", HttpStatus.BAD_REQUEST);
                vm.addError(error);
            }
        });

        return vm;
    }

}
