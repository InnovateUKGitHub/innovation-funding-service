package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.cofunder.resource.CofunderAssignmentResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDecisionResource;
import org.innovateuk.ifs.cofunder.resource.CofunderState;
import org.innovateuk.ifs.user.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Generates data from Competition Funders and attaches it to a competition
 */
public class CofunderDataBuilder extends BaseDataBuilder<Void, CofunderDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(CofunderDataBuilder.class);

    public CofunderDataBuilder withDecision(String funderEmail, long applicationId, CofunderState decision) {
        return with(data -> {
            doAs(ifsAdmin(), () -> {
                UserResource user = userService.findByEmail(funderEmail).getSuccess();
                CofunderAssignmentResource resource = cofunderAssignmentService.assign(user.getId(), applicationId).getSuccess();

                if (decision != CofunderState.CREATED) {
                    CofunderDecisionResource decisionResource = new CofunderDecisionResource();
                    decisionResource.setAccept(decision == CofunderState.ACCEPTED);
                    decisionResource.setComments("This application is extraordinary I'd " + (decision == CofunderState.ACCEPTED ? "love" : "hate") + " to fund it");
                    cofunderAssignmentService.decision(resource.getAssignmentId(), decisionResource).getSuccess();
                }

            });
        });
    }

    public static CofunderDataBuilder newCofunderData(ServiceLocator serviceLocator) {
        return new CofunderDataBuilder(Collections.emptyList(), serviceLocator);
    }

    private CofunderDataBuilder(List<BiConsumer<Integer, Void>> multiActions,
                                ServiceLocator serviceLocator) {

        super(multiActions, serviceLocator);
    }

    @Override
    protected CofunderDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Void>> actions) {
        return new CofunderDataBuilder(actions, serviceLocator);
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
