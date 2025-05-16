package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import com.sky.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderTask {
    /**
     * m每分钟检查一次是否存在超时未支付订单（下单超过15min未支付就代表超时，需要修改状态为已取消）
     */

    @Autowired
    OrdersMapper ordersMapper;

    @Scheduled(cron = "0 * * * * ?")
    public void processOutTimeOrder() {
        //1. 查询数据库orders表，条件：状态-未支付，下单时间大于当前时间-15min
        LocalDateTime time = LocalDateTime.now().minusMinutes(15);
        List<Orders> ordersList = ordersMapper.selectByStatusAndOrderTime(Orders.PENDING_PAYMENT,time);

        //log.info("查到的订单数：{}", ordersList);
        //2.如果查询到了数据，代表存在超时未支付的订单，需要修改订单的状态为status = 6
        if (ordersList != null || !ordersList.isEmpty()) {
            ordersList.forEach(orders -> {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                ordersMapper.update(orders);
            });
        }
    }

    /**
     * 每天凌晨1点检查一次订单表，查看是否存在派送中的订单，如果存在修改状态为已完成
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder(){
        //1. 查询数据库orders表，条件：状态-派送中, 下单时间 < 当前时间-1小时
        LocalDateTime time = LocalDateTime.now().minusHours(1);
        List<Orders> ordersList = ordersMapper.selectByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS,time);
        //2.如果查询到了数据，代表存在超时未支付的订单，需要修改订单的状态为status = 6
        if (ordersList != null || !ordersList.isEmpty()) {
            ordersList.forEach(orders -> {
                orders.setStatus(Orders.COMPLETED);
                orders.setDeliveryTime(time);
                ordersMapper.update(orders);
            });
        }



    }


}
