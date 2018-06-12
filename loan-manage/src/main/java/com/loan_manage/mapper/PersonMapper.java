package com.loan_manage.mapper;

import java.util.List;
import java.util.Map;

import com.loan_entity.manager_vo.ReviewVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import com.loan_entity.app.Person;
import com.loan_entity.app.PersonMode;
import com.loan_entity.manager_vo.CardPicInfoVo;
import com.loan_entity.manager_vo.PrivateVo;

public interface PersonMapper extends Mapper<Person>{

    Person selectPersonById(Integer personId);

    Person getPersonByPhone(String phone);

  //查询个人资料
    PersonMode getPersonInfoByBorr(String brroid);

    /**
     * 根据电话  身份证  姓名查询个人ID
     * @param map
     * @return
     */
    List<Integer> selectPersonId(Map<String,Object> map);
    /**
     * 查看基本信息
     * @param perId
     * @return
     */
    PrivateVo queryUserInfo(int perId);
    
    /**
     * 查看身份证信息
     * @param perId
     * @return
     */
    CardPicInfoVo queryCardPicById(int perId);

    /**
     * 查看黑名单
     * @param perId
     * @return
     */
    List<ReviewVo> getReviewVoBlackList(int perId);

    List<Person> selectPersonByBlackListNull();

    Integer selectPersonByBlackListNullCount();

    void updatePersonBlackList(@Param("personList") List<Integer> personList);
}