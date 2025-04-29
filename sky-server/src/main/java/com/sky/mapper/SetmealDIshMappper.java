package com.sky.mapper;

import com.sky.entity.SetmealDish;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealDIshMappper {
    Integer countByDishId(List<Long> dishIds);

    void insertBatch(List<SetmealDish> setmealDishList);

    List<SetmealDish> getById(Long id);

    Integer countStatus(Long id);

    void delete(Long id);
}
