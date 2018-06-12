package com.loan_manage.entity.arbitration;

import com.loan_manage.utils.Detect;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanzezhong on 2018/3/27.
 */

@Getter
@Setter
public class Files implements Serializable {
    /*****xxx借贷纠纷案仲裁申请书.docx*****/
    public String fileName;

    public static List<Files> getFiles(String... fileNames){
        List<Files> files = null ;
        if(Detect.notEmpty(fileNames)){
            files = new ArrayList();
            for (String fileName : fileNames){
                Files file = new Files();
                file.setFileName(fileName);
                files.add(file);
            }
        }
        return files;
    }
}
