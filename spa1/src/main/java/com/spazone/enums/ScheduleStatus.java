package com.spazone.enums;

public enum ScheduleStatus {
    SCHEDULED,    // The work shift has been scheduled but not yet completed.
    COMPLETED,    // The work shift has been successfully completed.
    CANCELLED,    // The work shift has been cancelled.
    NO_SHOW,      // The employee did not show up for the scheduled shift.
    ON_LEAVE,     // The employee is on approved leave (e.g., vacation, sick leave).
    PENDING,      // The schedule is awaiting confirmation or approval.
    RESCHEDULED   // The original schedule has been moved to a new time or date.
}
