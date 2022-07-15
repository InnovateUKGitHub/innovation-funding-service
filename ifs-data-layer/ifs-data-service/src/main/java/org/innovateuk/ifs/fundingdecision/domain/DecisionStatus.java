package org.innovateuk.ifs.fundingdecision.domain;

/**
 * enum to represents the possible states of a funding decision for a single application.
 * either it is funded, it is not funded, it is on hold, or it is not yet decided.
 */
public enum DecisionStatus {
	FUNDED,
	UNFUNDED,
	EOI_APPROVED,
	EOI_REJECTED,
	UNDECIDED,
	ON_HOLD
}
