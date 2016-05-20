package com.worth.ifs.application.domain;

/**
 * enum to represents the possible states of a funding decision for a single application.
 * either it is funded, it is not funded, or it is not yet decided.
 */
public enum FundingDecisionStatus {
	FUNDED,
	UNFUNDED,
	UNDECIDED;
}
