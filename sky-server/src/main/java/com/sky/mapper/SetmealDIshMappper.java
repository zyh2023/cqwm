package com.sky.mapper;

import java.util.List;

public interface SetmealDIshMappper {
    Integer countByDishId(List<Long> dishIds);
}
