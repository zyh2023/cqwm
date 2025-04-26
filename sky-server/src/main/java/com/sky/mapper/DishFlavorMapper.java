package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


public interface DishFlavorMapper {

    /**
     * 批量插入口味列表
     * @param dishFlavors
     */
    void insertBatch(List<DishFlavor> dishFlavors);
}
