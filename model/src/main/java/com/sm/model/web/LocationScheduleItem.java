package com.sm.model.web;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class LocationScheduleItem implements Serializable {
    private Date start;
    private Date stop;
    private List<Long> locationId;


}
