ALTER TABLE ym_order
  MODIFY COLUMN type VARCHAR(20)
CHARACTER SET utf8
COLLATE utf8_bin NOT NULL DEFAULT ''
  COMMENT '1.放款，2还款，3手续费,4还款(代收)5,服务费,6线下还款，7申请减免，8批量扣款, 9拉卡拉代扣， 10拉卡拉批量代扣'
  AFTER rl_state;

