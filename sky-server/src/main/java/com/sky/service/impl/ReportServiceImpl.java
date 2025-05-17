package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private UserMapper userMapper;
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        //1.准备日期列表数据 datalist ---》近七天
        List<LocalDate> dataList = getLocalDates(begin, end);
        //2.准备营业额列表数据 turnoverList
        List<Double> turnoverList = new ArrayList<>();
        // 营业额=订单状态已完成的订单金额
        // 查询orders表，条件：状态：已完成，下单时间
        dataList.forEach(date ->{
            Map map = new HashMap();
            map.put("status", Orders.COMPLETED);
            map.put("beginTime", LocalDateTime.of(date, LocalTime.MIN));
            map.put("endTime", LocalDateTime.of(date, LocalTime.MAX));
            Double turnover = ordersMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        });


        log.info("turnoverList = {}" , turnoverList);
        //3. 构造TurnoverReportVO对象并返回
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dataList,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }

    private static List<LocalDate> getLocalDates(LocalDate begin, LocalDate end) {
        List<LocalDate> dataList = new ArrayList<LocalDate>();
        while (!begin.isAfter(end)) {
            dataList.add(begin);
            begin = begin.plusDays(1);
        }
        log.info("dataList = {}" , dataList);
        return dataList;
    }

    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //1.准备日期列表数据 datalist ---》近七天
        List<LocalDate> dataList = getLocalDates(begin, end);
        // 2.构造newUserList数据，新增用户数据
        List<Integer> newUserList = new ArrayList<>();
        // 3.totalUserList数据，总用户列表
        List<Integer> totalUserLIst = new ArrayList<>();
        dataList.forEach(date ->{
            Map map = new HashMap();
            map.put("beginTime", LocalDateTime.of(date, LocalTime.MIN));
            map.put("endTime", LocalDateTime.of(date, LocalTime.MAX));
            Integer newUser = userMapper.coutByMap(map);
            newUser = newUser == null ? 0 : newUser;
            newUserList.add(newUser);
            // 3.totalUserList数据，总用户列表
            map.put("beginTime", null);
            Integer totalUser = userMapper.coutByMap(map);
            totalUser = totalUser == null ? 0 : totalUser;
            totalUserLIst.add(totalUser);
        });
        log.info("newUserList = {}" , newUserList);
        log.info("totalUserList = {}", totalUserLIst);
        return UserReportVO.builder()
                .dateList(StringUtils.join(dataList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .totalUserList(StringUtils.join(totalUserLIst,","))
                .build();
    }

    @Override
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        List<LocalDate> dateList = getLocalDates(begin, end);
        Integer totalOrderCount = 0;
        Integer validOrderCount = 0;
        //2.获取每日订单列表数 orderCountList
        // 查询order表数量，条件：下单时间 > 下单时间 <
        for (LocalDate date : dateList) {
            Map map = new HashMap();
            map.put("beginTime", LocalDateTime.of(date, LocalTime.MIN));
            map.put("endTime", LocalDateTime.of(date, LocalTime.MAX));
            Integer totalOrder = ordersMapper.coutByMap(map);
            orderCountList.add(totalOrder);
            //4.获取订单总数 totalOrderCount
            totalOrderCount += totalOrder;

            //3.获取每日订单列表数字  validOrderCountList
            map.put("status", Orders.COMPLETED);
            Integer validOrder = ordersMapper.coutByMap(map);
            validOrderCountList.add(validOrder);
            //5.获取有效订单数 validOrderCount
            validOrderCount += validOrder;
        }
        //6. 计算完成率 orderCompletionRate
        Double orderCompletionRatio;
        if (orderCountList.size() == 0 && validOrderCountList.size() == 0) {
            orderCompletionRatio = 0.0;
        } else {
            orderCompletionRatio = (double) validOrderCount / totalOrderCount;
        } ;
        //7. 封装
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRatio)
                .build();
    }

    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        //1.构造nameList,商品列表名称
        List<String> nameList = new ArrayList<>();

        List numberList = new ArrayList<>();
        Map map = new HashMap();
        map.put("status", Orders.COMPLETED);
        map.put("beginTime", LocalDateTime.of(begin, LocalTime.MIN));
        map.put("endTime", LocalDateTime.of(end, LocalTime.MAX));
        List<Map> list = ordersMapper.sumTop10(map);
        //2.构造numberList，商品销量列表
        for (Map m : list) {
            String name = (String) m.get("name");
            nameList.add(name);
            Object sumNum = m.get("sumNum");
            numberList.add(sumNum);
        };
        log.info("得到的nameList = {}" , nameList);
        log.info("得到的numberList = {}" , numberList);
        //3.封装
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList,","))
                .numberList(StringUtils.join(numberList,","))
                .build();
    }
}
