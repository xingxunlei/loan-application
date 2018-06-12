package com.jhh.dao;


import com.jhh.model.Msg;


public interface MsgMapper {

    int insertSelective(Msg record);

}