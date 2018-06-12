/**创建公司表*/
CREATE TABLE ym_zloan_company (
  id  int NOT NULL AUTO_INCREMENT COMMENT '公司ID' ,
  name  varchar(255) NULL COMMENT '公司名称' ,
  status  int NULL COMMENT '状态 0 未启用  1启用' ,
  PRIMARY KEY (id)
)COMMENT='公司表';

/**初始化公司数据*/
INSERT INTO ym_zloan_company(name,status) VALUES ('金互行-系统管理', '1');
INSERT INTO ym_zloan_company(name,status) VALUES ('金互行-风控部', '1');
INSERT INTO ym_zloan_company(name,status) VALUES ('金互行-贷后部', '1');
INSERT INTO ym_zloan_company(name,status) VALUES ('济南磐辰金融软件服务有限公司', '1');
INSERT INTO ym_zloan_company(name,status) VALUES ('上海诺煜投资管理有限公司', '1');
INSERT INTO ym_zloan_company(name,status) VALUES (' 济南磐辰金融软件服务有限公司', '1');
INSERT INTO ym_zloan_company(name,status) VALUES ('上海闻婷投资管理有限公司', '1');
INSERT INTO ym_zloan_company(name,status) VALUES ('山东富昱宸卓商务信息咨询有限公司', '1');
INSERT INTO ym_zloan_company(name,status) VALUES ('金互行-客服部', '1');
INSERT INTO ym_zloan_company(name,status) VALUES ('金互行-运营管理部', '1');
/**更改催收人员表结构*/
ALTER TABLE ds_collectors_level
  ADD COLUMN level_type  int NULL COMMENT '催收人员类型： 1为公司催收  2为外包催收' AFTER update_user,
  ADD COLUMN is_manage  int NULL COMMENT '是否是主管： 1是主管 0是非主管' AFTER level_type,
  ADD COLUMN phone  varchar(11) NULL COMMENT '电话号码' AFTER is_manage,
  ADD COLUMN password  varchar(255) NULL COMMENT '登录密码' AFTER user_sysno,
  ADD COLUMN dcl_type int NULL COMMENT '催收员类型：1自催2外包' AFTER phone;
/**更新所有贷后人员数据*/
UPDATE ds_collectors_level SET user_group_id=(select id from ym_zloan_company WHERE name='金互行-贷后部'),level_type=1,is_manage=0,dcl_type=1;
/**更新贷后失效的用户*/
UPDATE ds_collectors_level
SET status = 'B'
WHERE
  user_sysno not IN (
    'SH2086','A1183',
    'A1131','SH1960',
    'A1262','SH2143',
    'SH2148','SH2145',
    'SH2140','SH2142',
    'SH2135','SH2136',
    'SH2108','SH2125',
    'SH2109','SH2103',
    'SH2101','SH2008',
    'SH2026','A1497',
    'SH1990','SH1991','SH2121','SH2164','SH2172'
  );
/**更新济南磐辰金融软件服务有限公司用户*/
UPDATE ds_collectors_level SET
  status='A',
  user_group_id=(SELECT id from ym_zloan_company where name='济南磐辰金融软件服务有限公司'),
  level_type=2,
  is_manage=1,
  dcl_type=2
where user_sysno in('XN0014','XN0011');
/**更新山东富昱宸卓商务信息咨询有限公司用户*/
UPDATE ds_collectors_level SET
  status='A',
  user_group_id=(SELECT id from ym_zloan_company where name='山东富昱宸卓商务信息咨询有限公司'),
  level_type=2,
  is_manage=1,
  dcl_type=2
where user_sysno in('XN0010');
/**更新上海诺煜投资管理有限公司用户*/
UPDATE ds_collectors_level SET
  status='A',
  user_group_id=(SELECT id from ym_zloan_company where name='上海诺煜投资管理有限公司'),
  level_type=2,
  is_manage=1,
  dcl_type=2
where user_sysno in('XN0013');
/**更新上海闻婷投资管理有限公司用户*/
UPDATE ds_collectors_level SET
  status='A',
  user_group_id=(SELECT id from ym_zloan_company where name='上海闻婷投资管理有限公司'),
  level_type=2,
  is_manage=1,
  dcl_type=2
where user_sysno in('XN0012');
/*更新主管*/
update ds_collectors_level set level_type=1,is_manage=1,dcl_type=1 WHERE user_sysno in ('SH2086','A1183','A1131','SH1960');
/**初始化人员数据*/
INSERT INTO ds_collectors_level(user_sysno,user_name,password,status,user_group_id,level_type,is_manage,phone) values
  ('SH2128','刘洪明','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-系统管理'),10,1,'18621826816'),
  ('SH2116','万泽中','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-系统管理'),10,1,'13636569813'),
  ('A1563','束晨','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-系统管理'),10,1,'13564007713'),
  ('SH2009','陈振','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-系统管理'),10,1,'15151359765'),
  ('A0966','王鸽','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-系统管理'),10,1,'18916963326'),
  ('SH2129','吴汉宏','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-系统管理'),10,1,'15087002405'),
  ('SH2117','杨云华','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-系统管理'),10,1,'15720826020'),
  ('A0426','侯玉','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-风控部'),3,1,'15221831092'),
  ('A0342','刘勤彦','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-风控部'),3,1,'15921848679'),
  ('SH1887','祁静雅','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-风控部'),3,1,'18855952631'),
  ('A0392','吴俊','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-风控部'),3,1,'13512194209'),
  ('SH2011','陈华贇','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-风控部'),3,0,'13482283324'),
  ('A0880','董超','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-风控部'),3,0,'13916437859'),
  ('SH2153','马君','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-风控部'),3,0,'13816938907'),
  ('A1279','上官明维','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-风控部'),3,0,'13818082515'),
  ('A0393','苏捷毅','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-风控部'),3,0,'13918968441'),
  ('A0856','汪思慧','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-风控部'),3,0,'13120801082'),
  ('A1350','王辰明','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-风控部'),3,0,'13681962763'),
  ('A0392','吴俊','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-风控部'),3,0,'13512194209'),
  ('A1496','肖昉杰','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-风控部'),3,0,'13501772057'),
  ('A1427','许胤剑','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-风控部'),3,0,'13817885740'),
  ('SH2180','杨展堃','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-风控部'),3,0,'13052026180'),
  ('A1801','叶贇','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-风控部'),3,0,'13764552187'),
  ('SH2070','曹团团','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-客服部'),5,1,'13601834375'),
  ('SH2067','张琦','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-客服部'),5,1,'13270665702'),
  ('SH2161','柳宏伟','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-客服部'),5,1,'13918758258'),
  ('SH2178','沈隽','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-客服部'),5,1,'13817744347'),
  ('SH2190','奚慧敏','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-客服部'),5,1,'13585651042'),
  ('SH1964','俞欢庆','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-客服部'),5,1,'15921080606'),
  ('SH2191','赵炜','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-客服部'),5,1,'13501995074'),
  ('SH2050','卞直峰','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-运营管理部'),5,1,'13916385087'),
  ('A0264','陈卓','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-运营管理部'),5,1,'13564939939'),
  ('SH2040','程志屹','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-运营管理部'),5,1,'15502181102'),
  ('SH2047','江铭玮','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-运营管理部'),5,1,'18668197181'),
  ('SH2118','李元元','e10adc3949ba59abbe56e057f20f883e','A',(select id from ym_zloan_company where name='金互行-运营管理部'),5,1,'18516291862');
/**修改高级催收*/
UPDATE ds_collectors_level set is_manage=3 where user_sysno in('A1262','SH2143');
/**添加空白*/
INSERT INTO ds_collectors_level (guid, user_sysno, password, user_name, level, product_sysno, bedue_level, user_group_id, status, create_date, create_user, update_date, update_user, level_type, is_manage, phone, dcl_type) VALUES (NULL, '7777', NULL, '空白', NULL, NULL, NULL, '3', 'A', NULL, NULL, NULL, NULL, '1', '0', NULL, NULL);
/**删除重复数据*/
SELECT * from ds_collectors_level where user_sysno in (SELECT user_sysno FROM ds_collectors_level GROUP BY user_sysno HAVING count(user_sysno) > 1);

CREATE TABLE ym_zloan_company_borrow (
  id  int NOT NULL AUTO_INCREMENT ,
  borr_id  int NULL COMMENT '合同ID' ,
  company_id  int NULL COMMENT '公司ID' ,
  create_user  varchar(255) NULL COMMENT '创建人' ,
  create_date  datetime NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间' ,
  update_user  varchar(255) NULL COMMENT '更新人' ,
  update_date  datetime NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' ,
  version  int NULL COMMENT '版本锁',
  PRIMARY KEY (id),
  INDEX idx_borr_id (borr_id) USING BTREE ,
  INDEX idx_company_id (company_id) USING BTREE
)COMMENT='外包公司合同表';

CREATE TABLE ym_zloan_company_order (
  id int(11) NOT NULL AUTO_INCREMENT,
  order_id int(11) DEFAULT NULL COMMENT '订单ID',
  company_id int(11) DEFAULT NULL COMMENT '公司ID',
  create_user varchar(255) DEFAULT NULL COMMENT '创建人',
  create_date datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  INDEX idx_order_id (order_id) USING BTREE,
  INDEX idx_company_id (company_id) USING BTREE
) COMMENT='外包公司订单表';

/****更新认证流程****/
UPDATE ym_bpm_node n,ym_person p,ym_per_bpm b SET n.per_id = p.id WHERE b.id = n.bpm_id AND b.per_id = p.id AND n.per_id is NULL;
DELETE FROM ym_bpm_node WHERE per_id is NULL;

/*************更新ds_collectors_level_back  user_sysno 字段排序编码集,并创建唯一索引 JXL****************/
ALTER TABLE ds_collectors_level_back CHANGE COLUMN user_sysno user_sysno VARCHAR(20) NULL DEFAULT NULL COMMENT '人员id' COLLATE 'utf8_bin' AFTER guid;
ALTER TABLE ds_collectors_level_back   ADD UNIQUE INDEX dsb_user_sysno (user_sysno);

/*****更新产品****/
update ym_product set status=0 where id in(1,2,3,4);
update ym_product set status=1 where id in(5,6,7,8);

ALTER TABLE ym_borrow_list  ADD INDEX ym_borrow_list_act_repay_date (act_repay_date, borr_status);

/**同步外包催收单*/
INSERT INTO ym_zloan_company_borrow (borr_id,company_id,create_user,create_date) SELECT
 contract_sysno,
 (
   SELECT
     user_group_id
   FROM
     ds_collectors_level
   WHERE
     user_sysno = 'XN0010'
 ) as user_group_id,
 'SYS' as create_user ,now() as create_date
FROM
 ds_collectors_list
WHERE
 bedue_user_sysno = 'XN0010';
INSERT INTO ym_zloan_company_borrow (borr_id,company_id,create_user,create_date) SELECT
   contract_sysno,
   (
     SELECT
       user_group_id
     FROM
       ds_collectors_level
     WHERE
       user_sysno = 'XN0012'
   ) as user_group_id,

   'SYS' as create_user ,now() as create_date
 FROM
   ds_collectors_list
 WHERE
   bedue_user_sysno = 'XN0012';
INSERT INTO ym_zloan_company_borrow (borr_id,company_id,create_user,create_date) SELECT
   contract_sysno,
   (
     SELECT
       user_group_id
     FROM
       ds_collectors_level
     WHERE
       user_sysno = 'XN0011'
   ) as user_group_id,
   'SYS' as create_user ,now() as create_date
 FROM
   ds_collectors_list
 WHERE
   bedue_user_sysno = 'XN0011';
INSERT INTO ym_zloan_company_borrow (borr_id,company_id,create_user,create_date) SELECT
   contract_sysno,
   (
     SELECT
       user_group_id
     FROM
       ds_collectors_level
     WHERE
       user_sysno = 'XN0013'
   ) as user_group_id,
   'SYS' as create_user ,now() as create_date
 FROM
   ds_collectors_list
 WHERE
   bedue_user_sysno = 'XN0013';
INSERT INTO ym_zloan_company_borrow (borr_id,company_id,create_user,create_date) SELECT
  contract_sysno,
  (
  SELECT
  user_group_id
  FROM
  ds_collectors_level
  WHERE
  user_sysno = 'XN0014'
  ) as user_group_id,
  'SYS' as create_user ,now() as create_date
  FROM
  ds_collectors_list
  WHERE
  bedue_user_sysno = 'XN0014'
