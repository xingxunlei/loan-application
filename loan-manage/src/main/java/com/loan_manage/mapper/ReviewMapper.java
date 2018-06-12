package com.loan_manage.mapper;

import com.loan_entity.manager.Review;
import com.loan_entity.manager_vo.ReviewVo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ReviewMapper extends Mapper<Review> {
    List<ReviewVo> getReviewVoBlackList(int himid);
}
