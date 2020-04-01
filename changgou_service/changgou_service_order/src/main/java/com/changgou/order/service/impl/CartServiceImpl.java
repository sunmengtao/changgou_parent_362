package com.changgou.order.service.impl;

import com.changgou.entity.Constants;
import com.changgou.goods.feign.SkuFiegn;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SkuFiegn skuFiegn;

    @Autowired
    private SpuFeign spuFeign;


    /**
     * 购物车缓存结构设计
     * key: cart_username
     * val:
     *   key:skuId
     *   val:orderItem
     */
    @Override
    public void add(String username, String skuId, Integer num) {
        //1.判断购物车商品数据是否存在，如果存在则更新
        OrderItem orderItem = (OrderItem)redisTemplate.boundHashOps(Constants.REDIS_CART + username).get(skuId);
        if(orderItem!=null){
            orderItem.setNum(orderItem.getNum() + num);//设置缓存商品的最新数量
            if(orderItem.getNum()<=0){
                redisTemplate.boundHashOps(Constants.REDIS_CART + username).delete(skuId);
            }
            orderItem.setMoney(orderItem.getPrice() * orderItem.getNum()); //小计价格 = 单价*数量
            orderItem.setPayMoney(orderItem.getMoney());

        } else { //2.如果不存在则新增
            orderItem = buildOrderItem( skuId,  num);

        }

        //3.保存或更新orderItem到缓存中
        redisTemplate.boundHashOps(Constants.REDIS_CART + username).put(skuId, orderItem);
    }

    private OrderItem buildOrderItem(String skuId, Integer num) {
        if(num<=0){
            throw  new RuntimeException("商品第一次添加到购物车时数量必须大于0");
        }

        //根据skuId查询sku
        Sku sku = skuFiegn.findById(skuId);
        if(sku==null){
            throw new RuntimeException("商品不存在");
        }

        //根据spuId查询spu
        Spu spu = spuFeign.findById(sku.getSpuId());
        if(spu==null){
            throw new RuntimeException("商品不存在");
        }

        //封装OrderItem
        OrderItem orderItem = new OrderItem();
        orderItem.setSkuId(sku.getId());
        orderItem.setSpuId(spu.getId());
        orderItem.setCategoryId1(spu.getCategory1Id());
        orderItem.setCategoryId2(spu.getCategory2Id());
        orderItem.setCategoryId3(spu.getCategory3Id());
        orderItem.setPrice(sku.getPrice()); //商品单价
        orderItem.setNum(num);  //商品条目对应的商品数量
        orderItem.setMoney(sku.getPrice() * num);  //商品条目对应的小计价格
        orderItem.setPayMoney(orderItem.getMoney()); //支付价格
        orderItem.setChecked(false); //未选中
        orderItem.setWeight(sku.getWeight()); //重量
        orderItem.setImage(sku.getImage()); //商品图片
        orderItem.setName(sku.getName());  //商品名称

        return orderItem;
    }
}
