package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Override
    @Transactional
    public void addDish(DishDTO dto) {
        //1,构造菜品基本信息数据，将其存入dish表中
        Dish dish = new Dish();
        //拷贝属性值
        BeanUtils.copyProperties(dto,dish);
        //调用Mapper方法
        dishMapper.insert(dish);

        log.info("dish id:{}",dish.getId());

        //2.构造菜品口味列表数据，将其存入dish——flavor表中
        List<DishFlavor> dishFlavors = dto.getFlavors();
        //2.1关联菜品id
        dishFlavors.forEach(dishFlavor -> {
            dishFlavor.setDishId(dish.getId());
        });
        //2.2 调用mapper方法
        dishFlavorMapper.insertBatch(dishFlavors);
    }
}
