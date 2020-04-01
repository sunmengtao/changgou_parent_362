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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                return;//注意：此处一定要返回回去
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


    @Override
    public Map list(String username) {
        Map map = new HashMap();

        List<OrderItem> orderItemList = redisTemplate.boundHashOps(Constants.REDIS_CART + username).values();
        map.put("orderItemList", orderItemList);//购物车里商品条目的集合

        int totalNum = 0; //结算时的总数量
        int totalPrice = 0; //结算时的总价格
        
        if(orderItemList!=null && orderItemList.size()>0){
            for (OrderItem orderItem : orderItemList) {
                if(orderItem.isChecked()){ //如果商品已经勾选，那么才计算商品的数量及总价
                    totalNum += orderItem.getNum();
                    totalPrice += orderItem.getMoney(); // 注意，这里是总计价格，不是总计单价
                }
            }
        }
        map.put("totalNum", totalNum);
        map.put("totalPrice", totalPrice);

        return map;
    }


    @Override
    public void updateChecked(String username, String skuId, Boolean checked) {
        OrderItem orderItem = (OrderItem)redisTemplate.boundHashOps(Constants.REDIS_CART + username).get(skuId);
        if(orderItem==null){
            throw new RuntimeException("数据不存在！");
        }
        orderItem.setChecked(checked);
        //设置完状态后将商品更新到reis中去
        redisTemplate.boundHashOps(Constants.REDIS_CART + username).put(skuId, orderItem);
    }


    @Override
    public void delete(String username, String skuId) {
        OrderItem orderItem = (OrderItem)redisTemplate.boundHashOps(Constants.REDIS_CART + username).get(skuId);
        if(orderItem==null){
            throw new RuntimeException("数据不存在！");
        }
        redisTemplate.boundHashOps(Constants.REDIS_CART + username).delete(skuId);
    }
}
