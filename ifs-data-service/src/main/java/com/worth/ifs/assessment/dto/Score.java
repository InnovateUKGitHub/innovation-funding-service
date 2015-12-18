package com.worth.ifs.assessment.dto;


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
}
