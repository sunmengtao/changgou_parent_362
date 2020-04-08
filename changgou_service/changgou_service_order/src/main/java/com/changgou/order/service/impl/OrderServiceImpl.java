package com.changgou.order.service.impl;

import com.changgou.config.RabbitMQConfig;
import com.changgou.entity.Constants;
import com.changgou.goods.feign.SkuFiegn;
import com.changgou.order.dao.OrderItemMapper;
import com.changgou.order.dao.OrderLogMapper;
import com.changgou.order.dao.OrderMapper;
import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.pojo.OrderLog;
import com.changgou.order.service.CartService;
import com.changgou.order.service.OrderService;
import com.changgou.pay.feign.PayFeign;
import com.changgou.util.IdWorker;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CartService cartService;

    @Autowired
    private IdWorker idWorker;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private SkuFiegn skuFiegn;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PayFeign payFeign;

    @Autowired
    private OrderLogMapper orderLogMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 查询全部列表
     * @return
     */
    @Override
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @Override
    public Order findById(String id){
        return  orderMapper.selectByPrimaryKey(id);
    }


    /**
     * 增加
     * @param order
     */
    @Override
    public void add(Order order){
        orderMapper.insert(order);
    }


    /**
     * 修改
     * @param order
     */
    @Override
    public void update(Order order){
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(String id){
        orderMapper.deleteByPrimaryKey(id);
    }


    /**
     * 条件查询
     * @param searchMap
     * @return
     */
    @Override
    public List<Order> findList(Map<String, Object> searchMap){
        Example example = createExample(searchMap);
        return orderMapper.selectByExample(example);
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Order> findPage(int page, int size){
        PageHelper.startPage(page,size);
        return (Page<Order>)orderMapper.selectAll();
    }

    /**
     * 条件+分页查询
     * @param searchMap 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public Page<Order> findPage(Map<String,Object> searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Order>)orderMapper.selectByExample(example);
    }

    /**
     * 构建查询对象
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 订单id
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andEqualTo("id",searchMap.get("id"));
           	}
            // 支付类型，1、在线支付、0 货到付款
            if(searchMap.get("payType")!=null && !"".equals(searchMap.get("payType"))){
                criteria.andEqualTo("payType",searchMap.get("payType"));
           	}
            // 物流名称
            if(searchMap.get("shippingName")!=null && !"".equals(searchMap.get("shippingName"))){
                criteria.andLike("shippingName","%"+searchMap.get("shippingName")+"%");
           	}
            // 物流单号
            if(searchMap.get("shippingCode")!=null && !"".equals(searchMap.get("shippingCode"))){
                criteria.andLike("shippingCode","%"+searchMap.get("shippingCode")+"%");
           	}
            // 用户名称
            if(searchMap.get("username")!=null && !"".equals(searchMap.get("username"))){
                criteria.andLike("username","%"+searchMap.get("username")+"%");
           	}
            // 买家留言
            if(searchMap.get("buyerMessage")!=null && !"".equals(searchMap.get("buyerMessage"))){
                criteria.andLike("buyerMessage","%"+searchMap.get("buyerMessage")+"%");
           	}
            // 是否评价
            if(searchMap.get("buyerRate")!=null && !"".equals(searchMap.get("buyerRate"))){
                criteria.andLike("buyerRate","%"+searchMap.get("buyerRate")+"%");
           	}
            // 收货人
            if(searchMap.get("receiverContact")!=null && !"".equals(searchMap.get("receiverContact"))){
                criteria.andLike("receiverContact","%"+searchMap.get("receiverContact")+"%");
           	}
            // 收货人手机
            if(searchMap.get("receiverMobile")!=null && !"".equals(searchMap.get("receiverMobile"))){
                criteria.andLike("receiverMobile","%"+searchMap.get("receiverMobile")+"%");
           	}
            // 收货人地址
            if(searchMap.get("receiverAddress")!=null && !"".equals(searchMap.get("receiverAddress"))){
                criteria.andLike("receiverAddress","%"+searchMap.get("receiverAddress")+"%");
           	}
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if(searchMap.get("sourceType")!=null && !"".equals(searchMap.get("sourceType"))){
                criteria.andEqualTo("sourceType",searchMap.get("sourceType"));
           	}
            // 交易流水号
            if(searchMap.get("transactionId")!=null && !"".equals(searchMap.get("transactionId"))){
                criteria.andLike("transactionId","%"+searchMap.get("transactionId")+"%");
           	}
            // 订单状态
            if(searchMap.get("orderStatus")!=null && !"".equals(searchMap.get("orderStatus"))){
                criteria.andEqualTo("orderStatus",searchMap.get("orderStatus"));
           	}
            // 支付状态
            if(searchMap.get("payStatus")!=null && !"".equals(searchMap.get("payStatus"))){
                criteria.andEqualTo("payStatus",searchMap.get("payStatus"));
           	}
            // 发货状态
            if(searchMap.get("consignStatus")!=null && !"".equals(searchMap.get("consignStatus"))){
                criteria.andEqualTo("consignStatus",searchMap.get("consignStatus"));
           	}
            // 是否删除
            if(searchMap.get("isDelete")!=null && !"".equals(searchMap.get("isDelete"))){
                criteria.andEqualTo("isDelete",searchMap.get("isDelete"));
           	}

            // 数量合计
            if(searchMap.get("totalNum")!=null ){
                criteria.andEqualTo("totalNum",searchMap.get("totalNum"));
            }
            // 金额合计
            if(searchMap.get("totalMoney")!=null ){
                criteria.andEqualTo("totalMoney",searchMap.get("totalMoney"));
            }
            // 优惠金额
            if(searchMap.get("preMoney")!=null ){
                criteria.andEqualTo("preMoney",searchMap.get("preMoney"));
            }
            // 邮费
            if(searchMap.get("postFee")!=null ){
                criteria.andEqualTo("postFee",searchMap.get("postFee"));
            }
            // 实付金额
            if(searchMap.get("payMoney")!=null ){
                criteria.andEqualTo("payMoney",searchMap.get("payMoney"));
            }

        }
        return example;
    }


    @Transactional
    @Override
    public boolean submit(Order order) {
        String username = order.getUsername();
        //1.从当前用户的购物车获取数据
        Map cartMap = cartService.list(username);

        //2.业务数据状态前置判断
        if(cartMap.get("orderItemList")==null || cartMap.get("totalNum") ==null || cartMap.get("totalPrice") == null){
            logger.error("购物车数据不存在，username:{}", username);
            return false;
        }

        List<OrderItem> orderItemList = (List<OrderItem>) cartMap.get("orderItemList");
        if(orderItemList==null || orderItemList.size()==0){
            logger.error("购物车数据不存在，username:{}", username);
            return false;
        }

        //判断购物车商品选中的情况
        Integer totalNum = Integer.valueOf(String.valueOf(cartMap.get("totalNum")));
        Integer totalPrice = Integer.valueOf(String.valueOf(cartMap.get("totalPrice")));
        if(totalNum==0 || totalPrice==0){
            logger.error("购物车商品没有被选中，username:{}", username);
            return false;
        }

        //3.基于购物车数据创建订单并保存到DB
        order.setId(String.valueOf(idWorker.nextId())); //设置订单主键
        order.setTotalNum(totalNum); //选中的总商品数量
        order.setTotalMoney(totalPrice); //选中总商品价格
        order.setPayMoney(totalPrice);//支付金额
        order.setPreMoney(0);//优惠金额0
        order.setPostFee(0);//邮费0
        order.setIsDelete("0"); //未删除  [是否删除：0-未删除、1-已删除]
        order.setOrderStatus("0");//未完成  [订单状态：0-未完成、1-已支付、2-已发货、3-已完成、4-已关闭]
        order.setPayStatus("0"); //未支付 [支付状态：0-未支付、1-已支付、2-支付失败]
        order.setConsignStatus("0");//未发货 [发货状态：0-未发货、1-已发货、2-已收货]
        order.setSourceType("1");//来源于web [请求来源：1-web、2-app、3-微信公众号、4-微信小程序、5-H5手机页面]
        order.setBuyerRate("0");//未评价  [是否评价：0-未评价、1-已评价]
        order.setCreateTime(new Date());//订单创建时间
        order.setUpdateTime(new Date());//订单更新时间
        int orderInsertResult = orderMapper.insertSelective(order);//保存订单
        if(orderInsertResult==0){
            logger.error("订单新增失败，username:{}", username);
            return false;
        }

        //4.循环购物车列表数据，内部将选中的购物车商品条目保存到DB
        List<String> checkedSkuIdList = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            if(orderItem.isChecked()){
                orderItem.setId(String.valueOf(idWorker.nextId()));//设置商品条目主键
                orderItem.setOrderId(order.getId()); //设置关联的订单表的主键ID
                orderItem.setIsReturn("0");//未退货 [是否退货：0-未退货、1-已退货]
                int orderItemInsertResult = orderItemMapper.insertSelective(orderItem);
                if(orderItemInsertResult==0){
                    logger.error("商品条目新增失败，username:{}, orderId:{}", username, order.getId());
                    return false;
                }

                //5.商品条目对应的sku表数据的销量增加、库存减少。增加和减少的数量来自于商品条目中数量。
                Boolean decrResult = skuFiegn.decrCount(orderItem.getSkuId(), orderItem.getNum());
                if(!decrResult){
                    logger.error("商品的库存及销量更新失败，username:{}, orderId:{}", username, order.getId());
                    return false;
                }

                checkedSkuIdList.add(orderItem.getSkuId());
            }
        }

        //6.将待支付的订单保存到缓存中，方便跳转到支付时使用
        redisTemplate.boundValueOps(Constants.REDIS_ORDER_PAY + username).set(order);

        //7.将购物车中选中的商品从缓存中给删除掉
        for (String skuId : checkedSkuIdList) {
            redisTemplate.boundHashOps(Constants.REDIS_CART + username).delete(skuId);
        }

        // 8.添加消息到延迟队列,目的是等消息过期后,MQ的消费者自动处理订单的关闭
        rabbitTemplate.convertAndSend(RabbitMQConfig.RELAY_QUEUE, (Object) order.getId(), new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration("30000");
                return message;
            }
        });

        return true;
    }


    @Transactional
    @Override
    public void updateOrder(String orderId, String transactionId) {
        //1.根据商户ID查询微信支付订单，获取微信支付订单的最终交易状态
        Map<String,String> payMap = payFeign.queryOrder(orderId);
        if(StringUtils.isEmpty(payMap.get("trade_state"))){
            logger.error("微信支付订单交易状态不存在！orderId:{}",orderId);
            return;
        }

        //2.判断交易状态必须是支付成功或者支付失败中的两种
        String tradeState = payMap.get("trade_state");
        String payTime = payMap.get("time_end");
        if( !("SUCCESS".equals(tradeState)||"PAYERROR".equals(tradeState) ) ){
            logger.error("微信支付订单交易状态非法！orderId:{}",orderId);
            return;
        }

        //3.根据商户ID从表中查询订单数据，判断是否存在
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(order==null){
            logger.error("订单不存在！orderId:{}",orderId);
            return;
        }
        updateOrderCommon(orderId, transactionId, tradeState, payTime, order);

    }

    private void updateOrderCommon(String orderId, String transactionId, String tradeState, String payTime, Order order) {
        //4.防重复处理判断，判断订单的状态是否是未支付状态，如果不是，那么就拒绝处理
        if( !("0".equals(order.getPayStatus()) && "0".equals(order.getOrderStatus()) ) ){
            logger.error("订单已经更新过，无需重复处理！orderId:{}",orderId);
            return;
        }

        //5.根据微信支付的交易状态是支付成功还是支付失败来更新订单表对应的字段
        Order orderUpdate = new Order();
        orderUpdate.setId(order.getId());
        orderUpdate.setUpdateTime(new Date());
        try {
            if(StringUtils.isNotEmpty(payTime)){
                //设置支付时间，此时间要以微信支付的查询结果时间为准
                orderUpdate.setPayTime(DateUtils.parseDate(payTime, new String[]{"yyyyMMddHHmmss"}));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        orderUpdate.setOrderStatus("SUCCESS".equals(tradeState) ? "1" : "0"); //如果支付成功，那么订单状态是已支付，如果支付失败则保持未支付状态
        orderUpdate.setPayStatus("SUCCESS".equals(tradeState) ? "1" : "2"); //如果支付成功，那么支付状态是已支付，如果支付失败则是支付失败状态
        orderUpdate.setTransactionId(transactionId); //微信支付订单ID
        int c1 = orderMapper.updateByPrimaryKeySelective(orderUpdate);
        if(c1==0){
            logger.error("订单更新状态失败！orderId:{}",orderId);
            return;
        }

        //6.保存订单日志记录
        OrderLog orderLog = new OrderLog();
        orderLog.setId(String.valueOf(idWorker.nextId()));
        orderLog.setOrderId(orderId);
        orderLog.setOrderStatus(orderUpdate.getOrderStatus());
        orderLog.setPayStatus(orderUpdate.getPayStatus());
        orderLog.setOperater("system");
        orderLog.setOperateTime(new Date());
        orderLog.setRemarks("SUCCESS".equals(tradeState) ?  "订单支付成功" : "订单支付失败");
        int c2 = orderLogMapper.insertSelective(orderLog);
        if(c2==0){
            logger.error("订单日志保存失败！orderId:{}",orderId);
            return;
        }

        //7.删除缓存中待支付的订单
        redisTemplate.delete(Constants.REDIS_ORDER_PAY + order.getUsername());
    }

    @Transactional
    @Override
    public void closeOrder(String orderId) {
        //业务前置判断

        //调用微信支付查询交易状态,判断是否为空
        Map<String,String> payMap = payFeign.queryOrder(orderId);
        String tradeState = payMap.get("trade_state");
        if (StringUtils.isEmpty(tradeState)){
            logger.error("微信支付交易状态不存在!orderId:{}",orderId);
            throw new RuntimeException("微信支付交易状态不存在!");
        }

        //判断交易状态是否合法(支付成功,支付失败,未支付)
        if ("SUCCESS".equals(tradeState) || "PAYERROR".equals(tradeState) || "NOTPAY".equals(tradeState)){
            logger.error("微信支付交易状态不合法! ");
        }

        //根据ID查询数据库判断是否存在,以及判断状态

        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order==null){
            logger.error("微信支付交易状态不合法! ");
            throw new RuntimeException("微信支付交易状态不合法!");
        }

        //处理几种场景
        if ("NOTPAY".equals(tradeState)){
            //订单关闭
            payFeign.closeOrder(orderId);
            Map<String, String> respMap = payFeign.queryOrder(orderId);
            if (!"CLOSED".equals(respMap.get("trade_state"))){
                logger.error("微信订单关闭失败! orderId:{}",orderId);
                throw new RuntimeException("微信订单关闭失败!");
            }
            //更新表状态
            Order orderUpdate = new Order();
            orderUpdate.setId(orderId);
            orderUpdate.setOrderStatus("4");
            orderUpdate.setUpdateTime(new Date());
            orderUpdate.setCloseTime(new Date());
            int c1 = orderMapper.updateByPrimaryKeySelective(orderUpdate);
            if (c1==0){
                logger.error("订单更新失败! orderId:{}",orderId);
                throw new RuntimeException("订单更新失败!");
            }

            //库存恢复销量减少

            //根据订单ID查询订单条目列表,循环订单条目列表进行更新
            OrderItem cond = new OrderItem();
            cond.setOrderId(orderId);
            List<OrderItem> orderItemList = orderItemMapper.select(cond);
            if (orderItemList!=null && orderItemList.size()>0){
                for (OrderItem orderItem : orderItemList) {
                    Boolean flag = skuFiegn.incrCount(orderItem.getSkuId(), orderItem.getNum());
                    if (!flag){
                        logger.error("订单库存恢复失败! orderId:{}", orderId);
                        throw new RuntimeException("订单库存恢复失败!");
                    }
                }
            }

            //记录订单日志
            OrderLog orderLog = new OrderLog();
            orderLog.setId(String.valueOf(idWorker.nextId()));
            orderLog.setOrderStatus(orderId);
            orderLog.setOrderStatus("4");
            orderLog.setRemarks("订单已关闭");
            orderLog.setOperater("system");
            orderLog.setOperateTime(new Date());
            int c2 = orderLogMapper.insertSelective(orderLog);
            if (c2==0){
                logger.error("订单日志插入失败! orderId:{}",orderId);
                throw new RuntimeException("订单日志插入失败!");
            }
            //删除缓存
            redisTemplate.delete(Constants.REDIS_ORDER_PAY + order.getUsername());

        }else {
            //支付成功或失败的时候,可以复用微信支付回调时MQ消费者更新订单的部分代码
            String payTime = payMap.get("time_end");
            String transactionId = payMap.get("transaction_Id");
            updateOrderCommon(orderId, transactionId, tradeState, payTime, order);
        }

        // 关闭订单指定的时的考虑场景

        //场景1: 用户真的没有支付,一直到过期还没有支付,我们就负责处理订单关闭,更新表状态,库存恢复销量减少,记录订单日志,删除缓存

        //场景2: 用户在超时的瞬间程序正要执行到关闭订单接口时,用户支付了,支付完毕后,一种情况支付回调成功了,

        //场景3: 用户在超时的瞬间程序正要执行到关闭订单接口时,用户支付了,支付完毕后,一种情况支付回调没成功.我们就负责更新订单状态.


    }
}
