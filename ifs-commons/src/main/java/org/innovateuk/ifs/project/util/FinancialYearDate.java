package org.innovateuk.ifs.project.util;

import java.util.Calendar;
import java.util.Date;
/*
* A utility class, currently used to compute and compare fiscal year for spend profile summary at bottom of spend profile page.
* */
public class FinancialYearDate {

    private static final int FIRST_FISCAL_MONTH = Calendar.APRIL;

    private Calendar calendarDate;

    public FinancialYearDate(Calendar calendarDate) {
        this.calendarDate = calendarDate;
    }

    public FinancialYearDate(Date date) {
        this.calendarDate = Calendar.getInstance();
        this.calendarDate.setTime(date);
    }

    public int getFiscalMonth() {
        int month = calendarDate.get(Calendar.MONTH);
        int result = ((month - FIRST_FISCAL_MONTH - 1) % 12) + 1;
        if (result < 0) {
            result += 12;
        }
        return result;
    }

    public int getFiscalYear() {
        int month = calendarDate.get(Calendar.MONTH);
        int year = calendarDate.get(Calendar.YEAR);
        return (month >= FIRST_FISCAL_MONTH) ? year : year - 1;
    }

    public int getCalendarMonth() {
        return calendarDate.get(Calendar.MONTH);
    }

    public int getCalendarYear() {
        return calendarDate.get(Calendar.YEAR);
    }

}
