package com.loan.payment.mapper;

import com.loan_entity.manager.Order;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;


public interface OrderMapper extends Mapper<Order> {
    
    //根据订单号查询订单
    Order selectBySerial(String serial);
    //根据pid查询订单
    Order selectByPid(Integer pid);
    //查询订单状态为state,订单类型为type(代收)的数据，订单创建时间<任务运行的时间-30分钟
    List<Order> selectOrders(String state, String type, Integer minute);
    //根据合同id查合同编号
    String selectBorrNumById(Integer conctactId);
    //根据guid查询订单
    Order selectByGuid(String guid);
    
    int updateOrder(int orderId, String remark);
    
    int selectByConStatusTime(String conctactId, String state, String type);

    List<Order> selectPayOrders(Map<String,Object> map);

    int batchInsertGatherOrders(List<Order> orders);

    List<Order> selectBatchGatherOrders();
}