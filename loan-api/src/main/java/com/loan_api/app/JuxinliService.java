package com.loan_api.app;

import com.loan_entity.Juxinli.ReqDtoBasicInfo;
import com.loan_entity.app.NoteResult;
import com.loan_entity.manager_vo.ReqBackPhoneCheckVo;

/**
 * @author
 */
public interface JuxinliService {
    /**
     * @param record
     * @return
     */
    public NoteResult backPhoneCheckMessage(ReqBackPhoneCheckVo record);

    /**
     * @param Phone
     * @return
     */
    public NoteResult goBlack(String Phone);

    /**
     * @param reqDtoBasicInfo
     * @return
     */
    public NoteResult risk(ReqDtoBasicInfo reqDtoBasicInfo);

}
