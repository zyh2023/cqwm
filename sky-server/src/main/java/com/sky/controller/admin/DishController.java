package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "菜品相关接口")
@RequestMapping("/admin/dish")
@RestController
public class DishController {

    @Autowired
    private DishService dishService;

    @ApiOperation("新增菜品")
    @PostMapping
    public Result addDish(@RequestBody DishDTO dto) {
        log.info("新增菜品 dto:{}", dto);
        dishService.addDish(dto);
        return Result.success();
    }

    @ApiOperation("分页查询菜品")
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询菜品列表： {}", dishPageQueryDTO);
        PageResult pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("删除菜品")
    @DeleteMapping
    public Result deleteDish(@RequestParam List<Long> ids) {
        log.info("删除菜品的id：{}",ids);
        dishService.delete(ids);
        return Result.success();
    }

    @ApiOperation("根据id回显菜品")
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        log.info("回显菜品id：{}",id);
        DishVO dishVO =  dishService.getById(id);
        return Result.success(dishVO);
    }

    @ApiOperation("修改菜品")
    @PutMapping
    public Result update(@RequestBody DishDTO dto) {
        log.info("修改菜品：{}",dto);
        dishService.update(dto);
        return Result.success();
    }

    @ApiOperation("根据种类id回显")
    @GetMapping("/list")
    public Result getByCategoryId(@RequestParam Long categoryId) {
        log.info("根据种类回显：{}",categoryId);
        List<DishVO> dishVOList = dishService.getByCategoryId(categoryId);
        return Result.success(dishVOList);
    }

    @ApiOperation("修改菜品状态")
    @PostMapping("/status/{status}")
    public Result changeStatus(@RequestParam Long id, @PathVariable Integer status) {
        log.info("获得id：{}，获得status：{}",id,status);
        dishService.changeStatus(id,status);
        return Result.success();
    }

}
