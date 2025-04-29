package com.sky.controller.admin;


import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags="套餐相关接口")
@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    SetmealService setmealService;

    @ApiOperation("新增套餐")
    @PostMapping
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐：{}", setmealDTO);
        setmealService.save(setmealDTO);
        return Result.success();
    }

    @ApiOperation("根据ID套餐")
    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("根据ID查询套餐：{}",id);
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    @ApiOperation("套餐分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO dto){
        log.info("套餐查询：{}",dto);
        PageResult pageResult = setmealService.page(dto);
        return Result.success(pageResult);

    }

    @ApiOperation("改变套餐的状态")
    @PostMapping("/status/{status}")
    public Result changeStatus(@PathVariable Integer status,@RequestParam Long id){
        log.info("状态status：{}，id:{}",status,id);
        setmealService.changeStatus(status,id);
        return Result.success();
    }

    @ApiOperation("删除套餐")
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除套餐：{}",ids);
        setmealService.delete(ids);
        return Result.success();
    }

    @ApiOperation("修改套餐")
    @PutMapping
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐：{}",setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }
}
