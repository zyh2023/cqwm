package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.SetmealDIshMappper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    SetmealMapper setmealMapper;

    @Autowired
    SetmealDIshMappper setmealDishMappper;
    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        // 拷贝属性
        BeanUtils.copyProperties(setmealDTO, setmeal);

        // 插入 Setmeal
        setmealMapper.insert(setmeal);

        // 获取 setmealId
        Long setmealId = setmeal.getId();
        log.info("setmealId:{}", setmealId);

        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        if (setmealDishList != null && !setmealDishList.isEmpty()) {
            // 为每个 SetmealDish 设置 setmealId
            for (SetmealDish setmealDish : setmealDishList) {
                setmealDish.setSetmealId(setmealId);  // 确保 setmealId 被正确设置
            }

            // 批量插入 SetmealDish
            setmealDishMappper.insertBatch(setmealDishList);

            //判断新建的套餐中的菜品是否可售
            Integer count =  setmealDishMappper.countStatus(setmealId);
            if (count > 0) {throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED); }
        }
    }


    @Override
    public SetmealVO getById(Long id) {
        SetmealVO vo = new SetmealVO();
        Setmeal setmeal = setmealMapper.getById(id);
        log.info("验证是否是getbyid的问题:{}", setmeal);
        BeanUtils.copyProperties(setmeal, vo);
        List<SetmealDish> setmealDishList = setmealDishMappper.getById(id);
        vo.setSetmealDishes(setmealDishList);
        return vo;
    }

    @Override
    public PageResult page(SetmealPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(),dto.getPageSize());
        Page<SetmealVO> page = setmealMapper.list(dto);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void changeStatus(Integer status, Long id) {
        //如果当前状态是禁售的，检查套餐下的菜品是否是禁售状态
        if (status == 0) {
            Integer count =  setmealDishMappper.countStatus(id);
            //存在禁售的菜品，产生报错
            if (count > 0) {
                throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }

        setmealMapper.changeStatus(status,id);
    }

    @Transactional
    @Override
    public void delete(List<Long> ids) {
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getById(id);
            if (setmeal.getStatus() == 1)
            {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
            setmealDishMappper.delete(id);
            setmealMapper.delete(id);
        }
    }

    @Override
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        log.info("setmeal:{}", setmeal);
        setmealMapper.update(setmeal);
        setmealDishMappper.delete(setmealDTO.getId());
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        if (setmealDishList != null && !setmealDishList.isEmpty()) {
            for (SetmealDish setmealDish : setmealDishList) {
                setmealDish.setSetmealId(setmeal.getId());
            }
        }
        setmealDishMappper.insertBatch(setmealDishList);
    }
}
