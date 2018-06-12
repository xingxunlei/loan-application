package com.loan_server.manager_mapper;


public interface CollectorsListMapper {


    int updateCollectorsList(Integer borrId);

    String selectCollectUserByBorrId(Integer borrId);
}
