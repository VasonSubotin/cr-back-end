package com.sm.dao;

import com.sm.model.SmSchedules;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface ScheduleDao {
    SmSchedules getSmSchedulesById(String scheduleId, String accountId);

    SmSchedules getLastSmSchedulesByResourceId(Long resourceId, Long accountId);

    List<SmSchedules> getLastSmSchedulesByResourceId(Long resourceId, Long accountId, Date start, Date Stop);

    @Transactional(readOnly = false)
    SmSchedules saveSmSchedules(SmSchedules smSchedules);


}
