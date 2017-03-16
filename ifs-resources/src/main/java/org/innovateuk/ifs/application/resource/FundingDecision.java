package org.innovateuk.ifs.application.resource;

/**
 * Applications to a competition either receive funding or they do not.  These possibilities are expressed in this enum.
*/
public enum FundingDecision {
	FUNDED("Successful"),
	UNFUNDED("Unsuccessful"),
	UNDECIDED("-"),
	ON_HOLD("On hold");

	private String name;

	FundingDecision(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
