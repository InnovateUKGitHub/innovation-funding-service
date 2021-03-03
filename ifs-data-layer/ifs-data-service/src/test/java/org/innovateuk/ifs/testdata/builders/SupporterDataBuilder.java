package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.supporter.resource.SupporterAssignmentResource;
import org.innovateuk.ifs.supporter.resource.SupporterDecisionResource;
import org.innovateuk.ifs.supporter.resource.SupporterState;
import org.innovateuk.ifs.user.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Generates data from Competition Funders and attaches it to a competition
 */
public class SupporterDataBuilder extends BaseDataBuilder<Void, SupporterDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(SupporterDataBuilder.class);

    public SupporterDataBuilder withDecision(String funderEmail, long applicationId, SupporterState decision) {
        return with(data -> {
            doAs(ifsAdmin(), () -> {
                if (applicationService.getApplicationById(applicationId).getSuccess().isSubmitted()) {
                    UserResource user = userService.findByEmail(funderEmail).getSuccess();
                    SupporterAssignmentResource resource = supporterAssignmentService.assign(user.getId(), applicationId).getSuccess();

                    if (decision != SupporterState.CREATED) {
                        SupporterDecisionResource decisionResource = new SupporterDecisionResource();
                        decisionResource.setAccept(decision == SupporterState.ACCEPTED);
                        decisionResource.setComments("This application is extraordinary I'd " + (decision == SupporterState.ACCEPTED ? "love" : "hate") + " to fund it");
                        supporterAssignmentService.decision(resource.getAssignmentId(), decisionResource).getSuccess();
                    }
                }
            });
        });
    }

    public static SupporterDataBuilder newSupporterData(ServiceLocator serviceLocator) {
        return new SupporterDataBuilder(Collections.emptyList(), serviceLocator);
    }

    private SupporterDataBuilder(List<BiConsumer<Integer, Void>> multiActions,
                                ServiceLocator serviceLocator) {

        super(multiActions, serviceLocator);
    }

    @Override
    protected SupporterDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Void>> actions) {
        return new SupporterDataBuilder(actions, serviceLocator);
    }

    @Override
    protected Void createInitial() {
        return null;
    }

    @Override
    protected void postProcess(int index, Void instance) {
        super.postProcess(index, instance);
        LOG.info("Created Competition Funder");
    }
}
