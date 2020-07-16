package com.sm.dao;

import com.sm.model.SmSchedules;
import org.springframework.transaction.annotation.Transactional;

public interface ScheduleDao {
    SmSchedules getSmSchedulesById(String scheduleId, String accountId);

    SmSchedules getLastSmSchedulesByResourceId(Long resourceId, Long accountId);

    @Transactional(readOnly = false)
    SmSchedules saveSmSchedules(SmSchedules smSchedules);


}
