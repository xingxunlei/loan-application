package com.loan_manage.mapper;

import java.util.List;
import java.util.Map;

import com.loan_entity.manager.Order;
import com.loan_manage.entity.DownloadOrder;
import com.loan_manage.entity.OrderVo;
import tk.mybatis.mapper.common.Mapper;

public interface OrderMapper extends Mapper<Order> {
    
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
	
	List<Map<String, Object>> getOrders(Map<String, Object> paramMap);

	double getOrderMoney(Map<String, Object> map);

	List<DownloadOrder> selectDownloadOrder(Map<String, Object> paramMap);

	Long selectOrderVoInfoItem(Map<String, Object> paramMap);
	List<OrderVo> selectOrderVoInfo(Map<String, Object> paramMap);
}