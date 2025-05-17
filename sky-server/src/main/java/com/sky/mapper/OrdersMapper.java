package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrdersMapper {
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    @Update("update orders set status = #{orderStatus},pay_status = #{orderPaidStatus} ,checkout_time = #{checkOutTime} " + "where number = #{orderidL}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime checkOutTime, Long orderidL);

    Page<Orders> pageQuery(OrdersPageQueryDTO dto);

    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);


    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    @Select("select * from orders where status = #{pendingPayment} and order_time < #{time}")
    List<Orders> selectByStatusAndOrderTime(Integer pendingPayment, LocalDateTime time);

    @Select("select sum(orders.amount) from orders where status = #{status} and order_time between #{beginTime} and #{endTime}")
    Double sumByMap(Map map);

    Integer coutByMap(Map map);

    @MapKey("name")
    List<Map> sumTop10(Map map);
}
