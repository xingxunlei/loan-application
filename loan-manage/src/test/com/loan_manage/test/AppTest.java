package com.loan_manage.test;


import lombok.Getter;
import lombok.Setter;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;


public class AppTest {
    @Test
    public void testInitCompanySQL() throws Exception{

        InputStream is = new FileInputStream("E:\\wuhanhong\\user.xls");
        if (is != null) {
            HSSFWorkbook wb = new HSSFWorkbook(is);
            HSSFSheet sheet = wb.getSheetAt(0);
            List<UserInfo> userInfos = new ArrayList<>();
            List<String> companies = new ArrayList<>();
            for (Iterator ite = sheet.rowIterator(); ite.hasNext(); ) {
                HSSFRow row = (HSSFRow) ite.next();
                Cell cell = row.getCell(4);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                String cellValue = cell.getStringCellValue();
                if(!companies.contains(cellValue)){
                    companies.add(cellValue);
                }
            }

            StringBuffer insertSQL = new StringBuffer("INSERT INTO ym_zloan_company(name,status) VALUES");
            for(String company : companies){
                if(!"公司".equals(company)){
                    insertSQL.append("('").append(company).append("'").append(",1").append("),");
                }
            }
            System.out.println("插入公司SQL："+insertSQL);
        }
    }

    public void testDSUser() throws Exception {
        InputStream is = new FileInputStream("E:\\wuhanhong\\user.xls");
        if (is != null) {
            HSSFWorkbook wb = new HSSFWorkbook(is);
            HSSFSheet sheet = wb.getSheetAt(0);
            List<UserInfo> userInfos = new ArrayList<>();
            for (Iterator ite = sheet.rowIterator(); ite.hasNext(); ) {
                HSSFRow row = (HSSFRow) ite.next();

                String[] infoStrs = new String[6];
                for (int i = 0; i < 6; i++) {
                    Cell cell = row.getCell(i);
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    String cellValue = cell.getStringCellValue();
                    infoStrs[i] = cellValue;
                }
                userInfos.add(new UserInfo(infoStrs[0], infoStrs[1], infoStrs[2], infoStrs[3], infoStrs[4], infoStrs[5]));
            }

            String status = "A";
            String initPassword = "e10adc3949ba59abbe56e057f20f883e";

            StringBuffer insertUserSQL = new StringBuffer("INSERT INTO ds_collectors_level(" +
                    "user_sysno,user_name,password,status,user_group_id,level_type,is_manage,phone) values");
            for (int i = 1; i < userInfos.size(); i++) {
                UserInfo userInfo = userInfos.get(i);
                if ("金互行-系统管理".equals(userInfo.getCompany())
                        || "金互行-风控部".equals(userInfo.getCompany())
                        || "金互行-客服部".equals(userInfo.getCompany())
                        || "金互行-运营管理部".equals(userInfo.getCompany())
                        ) {
                    Integer companyId = 0;//companyMaps.get(userInfo.getCompany());
                    String userSysno = userInfo.getUserNo();
                    String userName = userInfo.getName();
                    String phone = userInfo.getMobile();
                    int isManage = 0;
                    int levelType = 0;
                    switch (userInfo.getCompany()) {
                        case "金互行-系统管理":
                            isManage = 1;
                            levelType = 10;
                            break;
                        case "金互行-风控部":
                            if ("风控管理员".equals(userInfo.getRole())) {
                                isManage = 1;
                                levelType = 3;
                            } else {
                                isManage = 0;
                                levelType = 3;
                            }
                            break;
                        case "金互行-客服部":
                            isManage = 1;
                            levelType = 5;
                            break;
                        case "金互行-运营管理部":
                            isManage = 1;
                            levelType = 4;
                            break;
                    }

                    insertUserSQL.append("(");
                    insertUserSQL.append("'").append(userSysno).append("',");
                    insertUserSQL.append("'").append(userName).append("',");
                    insertUserSQL.append("'").append(initPassword).append("',");
                    insertUserSQL.append("'").append(status).append("',");
                    insertUserSQL.append("").append(companyId).append(",");
                    insertUserSQL.append("").append(levelType).append(",");
                    insertUserSQL.append("").append(isManage).append(",");
                    insertUserSQL.append("'").append(phone).append("'");
                    insertUserSQL.append(")").append(",\r\n");
                }
                System.out.println(insertUserSQL);
            }
        }
    }

    @Test
    public void initUserInfo() throws Exception {

        Map<String,Integer> companyMaps = new HashMap<>();
        companyMaps.put("金互行-系统管理",1);
        companyMaps.put("金互行-风控部",2);
        companyMaps.put("金互行-贷后部",3);
        companyMaps.put("济南磐辰金融软件服务有限公司",4);
        companyMaps.put("上海诺煜投资管理有限公司",5);
        companyMaps.put(" 济南磐辰金融软件服务有限公司",6);
        companyMaps.put("上海闻婷投资管理有限公司",7);
        companyMaps.put("山东富昱宸卓商务信息咨询有限公司",8);
        companyMaps.put("金互行-客服部",9);
        companyMaps.put("金互行-运营管理部",10);

        InputStream is = new FileInputStream("E:\\wuhanhong\\user.xls");
        if (is != null) {
            HSSFWorkbook wb = new HSSFWorkbook(is);
            HSSFSheet sheet = wb.getSheetAt(0);
            List<UserInfo> userInfos = new ArrayList<>();
            for (Iterator ite = sheet.rowIterator(); ite.hasNext(); ) {
                HSSFRow row = (HSSFRow) ite.next();

                String[] infoStrs = new String[6];
                for (int i = 0; i < 6; i++) {
                    Cell cell = row.getCell(i);
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    String cellValue = cell.getStringCellValue();
                    infoStrs[i] = cellValue;
                }
                userInfos.add(new UserInfo(infoStrs[0],infoStrs[1],infoStrs[2],infoStrs[3],infoStrs[4],infoStrs[5]));
            }

            String status = "A";
            String initPassword = "e10adc3949ba59abbe56e057f20f883e";

            StringBuffer insertUserSQL = new StringBuffer("INSERT INTO ds_collectors_level(" +
                    "user_sysno,user_name,password,status,user_group_id,level_type,is_manage,phone) values");
            StringBuffer updateUserSQL = new StringBuffer();
            for (int i = 1 ; i < userInfos.size() ; i++) {
                UserInfo userInfo = userInfos.get(i);
                if("贷后部".equals(userInfo.getDepartment())){
                    updateUserSQL.append("update ds_collectors_level set phone='").append(userInfo.getMobile()).append("'").append(" where user_sysno='").append(userInfo.getUserNo()).append("' \r\n");
                }

                /*if ("金互行-系统管理".equals(userInfo.getCompany())
                        || "金互行-风控部".equals(userInfo.getCompany())
                        || "金互行-客服部".equals(userInfo.getCompany())
                        || "金互行-运营管理部".equals(userInfo.getCompany())
                        ) {
                    String companyId = "(select id from ym_zloan_company where name='"+userInfo.getCompany()+"')";
                    String userSysno = userInfo.getUserNo();
                    String userName = userInfo.getName();
                    String phone = userInfo.getMobile();
                    int isManage = 0;
                    int levelType = 0;
                    switch (userInfo.getCompany()) {
                        case "金互行-系统管理":
                            isManage = 1;
                            levelType = 10;
                            break;
                        case "金互行-风控部":
                            if ("风控管理员".equals(userInfo.getRole())) {
                                isManage = 1;
                                levelType = 3;
                            } else {
                                isManage = 0;
                                levelType = 3;
                            }
                            break;
                        case "金互行-客服部":
                            isManage = 1;
                            levelType = 5;
                            break;
                        case "金互行-运营管理部":
                            isManage = 1;
                            levelType = 4;
                            break;
                    }

                    insertUserSQL.append("(");
                    insertUserSQL.append("'").append(userSysno).append("',");
                    insertUserSQL.append("'").append(userName).append("',");
                    insertUserSQL.append("'").append(initPassword).append("',");
                    insertUserSQL.append("'").append(status).append("',");
                    insertUserSQL.append("").append(companyId).append(",");
                    insertUserSQL.append("").append(levelType).append(",");
                    insertUserSQL.append("").append(isManage).append(",");
                    insertUserSQL.append("'").append(phone).append("'");
                    insertUserSQL.append(")").append(",\r\n");
                }
                System.out.println(insertUserSQL);*/
            }
            System.out.println(updateUserSQL.toString());
        }
    }


    @Getter
    @Setter
    class UserInfo {
        private String name;
        private String userNo;
        private String mobile;
        private String department;
        private String company;
        private String password;
        private String role;

        public UserInfo(String name, String userNo, String mobile, String department, String company, String role) {
            this.name = name;
            this.userNo = userNo;
            this.mobile = mobile;
            this.department = department;
            this.company = company;
            this.role = role;
        }

        @Override
        public String toString() {
            return "UserInfo{" +
                    "name='" + name + '\'' +
                    ", userNo='" + userNo + '\'' +
                    ", mobile='" + mobile + '\'' +
                    ", department='" + department + '\'' +
                    ", company='" + company + '\'' +
                    '}';
        }
    }
}
