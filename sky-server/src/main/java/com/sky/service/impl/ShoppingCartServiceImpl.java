package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        // 创建shoppingcart对象
        ShoppingCart shoppingCart = new ShoppingCart();
        // 拷贝属性值
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);

        // 1.判断该商品是否已经存在购物车--条件：dishId+dishFlavor+userId
        // 只查当前用户自己的购物车
        shoppingCart.setUserId(BaseContext.getCurrentId());
        ShoppingCart cart = shoppingCartMapper.selectBy(shoppingCart);
        if (cart == null) {//代表购物车没有该商品数据
            // 2.补充缺失的属性值
            // 判断是新增套餐还是新增菜品
            if (shoppingCartDTO.getDishId() != null) {
                //根据菜品id查询菜品表，获取菜品相关信息
                Dish dish = dishMapper.selectById(shoppingCartDTO.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setImage(dish.getImage());
            } else{
                Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setImage(setmeal.getImage());
            }
            shoppingCart.setNumber(1);// 判断该商品是否已经存在购物车
            shoppingCart.setCreateTime(LocalDateTime.now());
            //3. 将商品数据存入到shopping_cart表
            shoppingCartMapper.insert(shoppingCart);
        }else{  //代表购物车有该商品数据

            //4. 将原来的购物车商品数量+1，调用mapper更新方法
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.update(cart);

        }



        // 最终目的：将用户添加的商品存入到购物车表中
    }

    @Override
    public List<ShoppingCart> list() {
        return shoppingCartMapper.list(BaseContext.getCurrentId());
    }

    @Override
    public void clean() {
        shoppingCartMapper.clean(BaseContext.getCurrentId());
    }

    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        // 拷贝属性值
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        ShoppingCart cart = shoppingCartMapper.selectBy(shoppingCart);
        if (cart.getNumber() == 1) {
            shoppingCartMapper.delete(cart);
        }else{
            cart.setNumber(cart.getNumber() - 1);
        }
    }
}
