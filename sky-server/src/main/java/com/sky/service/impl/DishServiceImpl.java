package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDIshMappper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
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
    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDIshMappper setmealDIshMappper;

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

    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        //1.设置分页参数
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());

        //2.调用mapper的列表查询方法，墙转成Page
        Page<DishVO> page = dishMapper.list(dishPageQueryDTO);
        //3.封装PageResult对象并返回
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Transactional
    @Override
    public void delete(List<Long> ids) {
        //1.删除菜品之前，需要判断菜品是否起售，起售不允许删除
        ids.forEach(id -> {
            Dish dish = dishMapper.selectById(id);
            if (dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });
        //2.需要判断菜品是否被套餐关联，关联了也不允许删除
        Integer count = setmealDIshMappper.countByDishId(ids);
        if (count > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //3.删除菜品基本信息dish表
        dishMapper.deleteBatch(ids);
        //4.删除菜品口味信息列表 dish_flavor表
        dishFlavorMapper.deleteBatch(ids);

    }

    @Override
    public DishVO getById(Long id) {
        DishVO dishVO = new DishVO();
        //根据id查询基本信息,封装到dishVo中
        Dish dish = dishMapper.selectById(id);
        BeanUtils.copyProperties(dish,dishVO);
        //根据id查询口味列表信息，封装
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectBydish(id);
        dishVO.setFlavors(dishFlavors);
        //构造vo对象并返回
        return dishVO;
    }
}
