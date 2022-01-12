package org.innovateuk.ifs.project.finance.resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckPartnerStatusResourceBuilder.newFinanceCheckPartnerStatusResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckSummaryResourceBuilder.newFinanceCheckSummaryResource;
import static org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResourceTest.Parameter.partnerStates;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class FinanceCheckSummaryResourceTest {
    private final Parameter parameter;

    public FinanceCheckSummaryResourceTest(Parameter parameter) {
        this.parameter = parameter;
    }

    @Test
    public void isAllEligibilityAndViabilityInReview() {
        FinanceCheckSummaryResource resource = newFinanceCheckSummaryResource()
                .withPartnerStatusResources(newFinanceCheckPartnerStatusResource()
                        .withEligibility(parameter.eligibilityStates)
                        .withViability(parameter.viabilityStates)
                        .build(parameter.getNumberOfPartners())
                )
                .build();

        assertEquals(parameter.expectedAllEligibilityAndViabilityInReview, resource.isAllEligibilityAndViabilityInReview());

    }

    @Parameterized.Parameters
    public static Collection parameters() {
        return asList(
                // 1 partner
                partnerStates(true, EligibilityState.REVIEW, ViabilityState.REVIEW),
                partnerStates(true, EligibilityState.NOT_APPLICABLE, ViabilityState.REVIEW),
                partnerStates(true, EligibilityState.REVIEW, ViabilityState.NOT_APPLICABLE),
                partnerStates(true, EligibilityState.NOT_APPLICABLE, ViabilityState.NOT_APPLICABLE),

                partnerStates(false, EligibilityState.APPROVED, ViabilityState.REVIEW),
                partnerStates(false, EligibilityState.REVIEW, ViabilityState.APPROVED),
                partnerStates(false, EligibilityState.APPROVED, ViabilityState.NOT_APPLICABLE),
                partnerStates(false, EligibilityState.NOT_APPLICABLE, ViabilityState.APPROVED),
                partnerStates(false, EligibilityState.APPROVED, ViabilityState.APPROVED),

                // 2 partners
                partnerStates(true,
                        EligibilityState.REVIEW, ViabilityState.REVIEW,
                        EligibilityState.REVIEW, ViabilityState.REVIEW),
                partnerStates(true,
                        EligibilityState.NOT_APPLICABLE, ViabilityState.REVIEW,
                        EligibilityState.REVIEW, ViabilityState.REVIEW),
                partnerStates(true,
                        EligibilityState.REVIEW, ViabilityState.NOT_APPLICABLE,
                        EligibilityState.REVIEW, ViabilityState.REVIEW),
                partnerStates(true,
                        EligibilityState.REVIEW, ViabilityState.REVIEW,
                        EligibilityState.NOT_APPLICABLE, ViabilityState.NOT_APPLICABLE),

                partnerStates(false,
                        EligibilityState.APPROVED, ViabilityState.REVIEW,
                        EligibilityState.REVIEW, ViabilityState.REVIEW),
                partnerStates(false,
                        EligibilityState.REVIEW, ViabilityState.APPROVED,
                        EligibilityState.REVIEW, ViabilityState.REVIEW),
                partnerStates(false,
                        EligibilityState.REVIEW, ViabilityState.REVIEW,
                        EligibilityState.APPROVED, ViabilityState.REVIEW),
                partnerStates(false,
                        EligibilityState.REVIEW, ViabilityState.REVIEW,
                        EligibilityState.REVIEW, ViabilityState.APPROVED),
                partnerStates(false,
                        EligibilityState.APPROVED, ViabilityState.REVIEW,
                        EligibilityState.REVIEW, ViabilityState.APPROVED),
                partnerStates(false,
                        EligibilityState.APPROVED, ViabilityState.APPROVED,
                        EligibilityState.REVIEW, ViabilityState.APPROVED),
                partnerStates(false,
                        EligibilityState.APPROVED, ViabilityState.APPROVED,
                        EligibilityState.APPROVED, ViabilityState.APPROVED)

        );
    }

    static class Parameter {
        private final boolean expectedAllEligibilityAndViabilityInReview;
        private final EligibilityState[] eligibilityStates;
        private final ViabilityState[] viabilityStates;

        private Parameter(boolean expectedAllEligibilityAndViabilityInReview,
                          EligibilityState[] eligibilityStates,
                          ViabilityState[] viabilityStates) {
            if (eligibilityStates.length != viabilityStates.length) throw new IllegalArgumentException();
            this.expectedAllEligibilityAndViabilityInReview = expectedAllEligibilityAndViabilityInReview;
            this.eligibilityStates = eligibilityStates;
            this.viabilityStates = viabilityStates;
        }

        static Parameter partnerStates(boolean expectedAllEligibilityAndViabilityInReview,
                                       EligibilityState es1, ViabilityState vs1) {
            return new Parameter(expectedAllEligibilityAndViabilityInReview, new EligibilityState[] {es1}, new ViabilityState[] {vs1});
        }

        static Parameter partnerStates(boolean expectedAllEligibilityAndViabilityInReview,
                                       EligibilityState es1, ViabilityState vs1,
                                       EligibilityState es2, ViabilityState vs2) {
            return new Parameter(expectedAllEligibilityAndViabilityInReview, new EligibilityState[] {es1, es2}, new ViabilityState[] {vs1, vs2});
        }

        private int getNumberOfPartners() {
            return eligibilityStates.length;
        }
    }
}