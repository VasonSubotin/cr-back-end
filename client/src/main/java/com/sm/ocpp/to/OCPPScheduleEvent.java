package com.sm.ocpp.to;

import java.util.Date;

public class OCPPScheduleEvent {
    private Date date;
    private boolean enable;

    public OCPPScheduleEvent(Date date, boolean enable) {
        this.date = date;
        this.enable = enable;
    }

    public Date getDate() {
        return date;
    }

    public boolean isEnable() {
        return enable;
    }
}
