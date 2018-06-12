package com.loan_entity.manager;

import lombok.Data;

@Data
public class Download {
    //正在下载数
    private Integer totalCount;
    //可下载条数
    private Integer downloadCount;
}
