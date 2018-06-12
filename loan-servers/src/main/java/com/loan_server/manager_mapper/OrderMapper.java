package com.loan_server.manager_mapper;

import java.util.List;
import java.util.Map;

import com.loan_entity.manager.Order;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);
    
    //根据订单号查询订单
    Order selectBySerial(String serial);
    //根据pid查询订单
    Order selectByPid(Integer pid);
    //查询订单状态为state,订单类型为type(代收)的数据，订单创建时间<任务运行的时间-30分钟
    List<Order> selectOrders(String state,String type,Integer minute);
    //根据合同id查合同编号
    String selectBorrNumById(Integer conctactId);
    //根据guid查询订单
    Order selectByGuid(String guid);
    
    int updateOrder(int orderId,String remark);
    
    int selectByConStatusTime(String conctactId,String state,String type);

    List<Order> selectPayOrders(String conctactId, String rlState);

	/**
	 * 根据参数查询订单
	 * 
	 * @param args
	 * @return
	 */
	List<Order> getOrdersByArgs(Map<String, Object> args);
}