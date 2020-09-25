package com.sm.dao;

import com.sm.model.SmScheduleType;
import com.sm.model.SmSchedules;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface ScheduleDao {
    SmSchedules getSmSchedulesById(Long scheduleId, Long accountId);


    SmSchedules getLastSmSchedulesByResourceIdAndType(Long resourceId, Long accountId, SmScheduleType type);

    List<SmSchedules> getNoIntervalSmSchedulesByResourceIdAndDateBetween(Long resourceId, Long accountId, Date start, Date Stop, SmScheduleType type);

    List<SmSchedules> getSmSchedulesByResourceIdAndDateBetweenAndType(Long resourceId, Long accountId, Date start, Date stop, SmScheduleType type);

    @Transactional(readOnly = false)
    SmSchedules saveSmSchedules(SmSchedules smSchedules);


}
