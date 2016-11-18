package com.worth.ifs.commons.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;

/**
 * A light transport format for java.util.LocalDates
 */
public class LocalDateResource {

    private int day;
    private int month;
    private int year;

    LocalDateResource() {
        // for JSON marshalling
    }

    public LocalDateResource(LocalDate date) {
        this(date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }

    public LocalDateResource(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @JsonIgnore
    public LocalDate getLocalDate() {
        return LocalDate.of(year, month, day);
    }
}
