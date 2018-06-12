package com.loan_server.app_mapper;

import com.loan_entity.app.Bpm;
import com.loan_entity.app.PersonNotify;
import org.apache.ibatis.annotations.Param;

public interface PersonNotifyMapper {

    int insert(PersonNotify record);

    int update(PersonNotify record);

    PersonNotify selectByPersonId(Integer perId);

    void resetStatusByNotifyId(String notifyId);
}