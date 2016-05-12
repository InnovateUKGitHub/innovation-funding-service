package com.worth.ifs.assessment.resource;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Score {
    private int total;
    private int possible;
    private int percentage;

    public Score(int possible, int total) {
        this.possible = possible;
        this.total = total;
        this.percentage = possible == 0 ? 0 : (total * 100) / possible;
    }

    public Score(){
    	// no-arg constructor
    }

    public int getTotal() {
        return total;
    }

    public int getPossible() {
        return possible;
    }

    public int getPercentage() {
        return percentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Score score = (Score) o;

        return new EqualsBuilder()
                .append(total, score.total)
                .append(possible, score.possible)
                .append(percentage, score.percentage)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(total)
                .append(possible)
                .append(percentage)
                .toHashCode();
    }
}
