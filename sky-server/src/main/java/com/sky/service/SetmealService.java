package com.sky.service;

import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    void save(SetmealDTO setmealDTO);

    SetmealVO getById(Long id);

    PageResult page(SetmealPageQueryDTO dto);

    void changeStatus(Integer status, Long id);

    void delete(List<Long> ids);

    void update(SetmealDTO setmealDTO);
}
