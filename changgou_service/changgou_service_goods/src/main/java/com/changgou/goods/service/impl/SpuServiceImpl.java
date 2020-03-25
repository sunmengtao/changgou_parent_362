package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Constants;
import com.changgou.goods.dao.*;
import com.changgou.goods.pojo.*;
import com.changgou.goods.service.SpuService;
import com.changgou.util.IdWorker;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 查询全部列表
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @Override
    public Spu findById(String id){
        return  spuMapper.selectByPrimaryKey(id);
    }


    /**
     * 增加
     * @param spu
     */
    @Override
    public void add(Spu spu){
        spuMapper.insert(spu);
    }


    /**
     * 修改
     * @param spu
     */
    @Override
    public void update(Spu spu){
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(String id){
        spuMapper.deleteByPrimaryKey(id);
    }


    /**
     * 条件查询
     * @param searchMap
     * @return
     */
    @Override
    public List<Spu> findList(Map<String, Object> searchMap){
        Example example = createExample(searchMap);
        return spuMapper.selectByExample(example);
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Spu> findPage(int page, int size){
        PageHelper.startPage(page,size);
        return (Page<Spu>)spuMapper.selectAll();
    }

    /**
     * 条件+分页查询
     * @param searchMap 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public Page<Spu> findPage(Map<String,Object> searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Spu>)spuMapper.selectByExample(example);
    }

    /**
     * 构建查询对象
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 主键
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andEqualTo("id",searchMap.get("id"));
           	}
            // 货号
            if(searchMap.get("sn")!=null && !"".equals(searchMap.get("sn"))){
                criteria.andEqualTo("sn",searchMap.get("sn"));
           	}
            // SPU名
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
           	}
            // 副标题
            if(searchMap.get("caption")!=null && !"".equals(searchMap.get("caption"))){
                criteria.andLike("caption","%"+searchMap.get("caption")+"%");
           	}
            // 图片
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
           	}
            // 图片列表
            if(searchMap.get("images")!=null && !"".equals(searchMap.get("images"))){
                criteria.andLike("images","%"+searchMap.get("images")+"%");
           	}
            // 售后服务
            if(searchMap.get("saleService")!=null && !"".equals(searchMap.get("saleService"))){
                criteria.andLike("saleService","%"+searchMap.get("saleService")+"%");
           	}
            // 介绍
            if(searchMap.get("introduction")!=null && !"".equals(searchMap.get("introduction"))){
                criteria.andLike("introduction","%"+searchMap.get("introduction")+"%");
           	}
            // 规格列表
            if(searchMap.get("specItems")!=null && !"".equals(searchMap.get("specItems"))){
                criteria.andLike("specItems","%"+searchMap.get("specItems")+"%");
           	}
            // 参数列表
            if(searchMap.get("paraItems")!=null && !"".equals(searchMap.get("paraItems"))){
                criteria.andLike("paraItems","%"+searchMap.get("paraItems")+"%");
           	}
            // 是否上架
            if(searchMap.get("isMarketable")!=null && !"".equals(searchMap.get("isMarketable"))){
                criteria.andEqualTo("isMarketable",searchMap.get("isMarketable"));
           	}
            // 是否启用规格
            if(searchMap.get("isEnableSpec")!=null && !"".equals(searchMap.get("isEnableSpec"))){
                criteria.andEqualTo("isEnableSpec", searchMap.get("isEnableSpec"));
           	}
            // 是否删除
            if(searchMap.get("isDelete")!=null && !"".equals(searchMap.get("isDelete"))){
                criteria.andEqualTo("isDelete",searchMap.get("isDelete"));
           	}
            // 审核状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andEqualTo("status",searchMap.get("status"));
           	}

            // 品牌ID
            if(searchMap.get("brandId")!=null ){
                criteria.andEqualTo("brandId",searchMap.get("brandId"));
            }
            // 一级分类
            if(searchMap.get("category1Id")!=null ){
                criteria.andEqualTo("category1Id",searchMap.get("category1Id"));
            }
            // 二级分类
            if(searchMap.get("category2Id")!=null ){
                criteria.andEqualTo("category2Id",searchMap.get("category2Id"));
            }
            // 三级分类
            if(searchMap.get("category3Id")!=null ){
                criteria.andEqualTo("category3Id",searchMap.get("category3Id"));
            }
            // 模板ID
            if(searchMap.get("templateId")!=null ){
                criteria.andEqualTo("templateId",searchMap.get("templateId"));
            }
            // 运费模板id
            if(searchMap.get("freightId")!=null ){
                criteria.andEqualTo("freightId",searchMap.get("freightId"));
            }
            // 销量
            if(searchMap.get("saleNum")!=null ){
                criteria.andEqualTo("saleNum",searchMap.get("saleNum"));
            }
            // 评论数
            if(searchMap.get("commentNum")!=null ){
                criteria.andEqualTo("commentNum",searchMap.get("commentNum"));
            }

        }
        return example;
    }

    @Transactional
    @Override
    public void addGoods(Goods goods) {
        //1.保存spu
        goods.getSpu().setId(String.valueOf(idWorker.nextId()));//雪花算法成成ID
        spuMapper.insertSelective(goods.getSpu());

        //2.保存skuList
        saveGoods(goods);
    }

    private void saveGoods(Goods goods) {
        Spu spu = goods.getSpu();
        Integer category3Id = spu.getCategory3Id();
        Category category = categoryMapper.selectByPrimaryKey(category3Id);//根据第三级ID查询分类信息
        Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());//根据品牌ID查询品牌信息

        //处理分类与品牌表的关联关系
        if(brand!=null && category!=null){
            CategoryBrand categoryBrand = new CategoryBrand();
            categoryBrand.setBrandId(brand.getId());
            categoryBrand.setCategoryId(category3Id);
            //1.根据分类ID和品牌ID从关联表查数据
            int count = categoryBrandMapper.selectCount(categoryBrand);
            //2.如果查询的结果条数等于0，那么说明没有关联关系，需要新增关联关系
            if(count==0){
                categoryBrandMapper.insertSelective(categoryBrand);
            }
        }

        List<Sku> skuList = goods.getSkuList();
        for (Sku sku : skuList) {
            sku.setId(String.valueOf(idWorker.nextId())); //雪花算法成成ID
            if(StringUtils.isEmpty(sku.getSpec())){ //特殊处理spec传值为空的情况，如果为空需要设置为{}
                sku.setSpec("{}");
            }
            String name = spu.getName();
            Map<String,String> specMap = JSON.parseObject(sku.getSpec(), Map.class); //将规格数据转换为MAP，方便取出所有规则的值
            if(specMap.size()>0){
               for(String key : specMap.keySet()){
                   String specVal = specMap.get(key);
                   name += " " + specVal;
               }
            }
            sku.setName(name); //sku名称，名称拼接规则： spu名称 + 所有规格值 （中间通过空格拼接）
            sku.setCreateTime(new Date()); //创建时间
            sku.setUpdateTime(new Date()); //更新时间
            sku.setSpuId(spu.getId());
            if(category!=null){
                sku.setCategoryId(category3Id); //三级分类ID
                sku.setCategoryName(category.getName()); //三级分类名称
            }
            if(brand!=null){
                sku.setBrandName(brand.getName()); //品牌名称
            }

            skuMapper.insertSelective(sku);
        }
    }


    @Override
    public Goods findBySpuId(String spuId) {
        //根据spuId查询spu
        Spu spu = spuMapper.selectByPrimaryKey(spuId);

        //根据spuId查询sku列表
        Sku skuCond = new Sku();
        skuCond.setSpuId(spuId);
        List<Sku> skuList = skuMapper.select(skuCond);

        //封装goods
        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skuList);
        
        
        return goods;
    }


    @Transactional
    @Override
    public void updateGoods(Goods goods) {
        //根据主键全量更新spu数据
        spuMapper.updateByPrimaryKeySelective(goods.getSpu());

        //根据spuId将sku数据删除
        Sku skuCond = new Sku();
        skuCond.setSpuId(goods.getSpu().getId());
        skuMapper.delete(skuCond);

        //将所有的sku数据新增
        saveGoods(goods);
    }


    @Override
    public void auditGoods(String spuId) {
        //查询spu商品
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if(spu==null){
            throw new RuntimeException("数据不存在！");
        }
        //如何审核状态不是为审核的，抛出异常
        if(!"0".equals(spu.getStatus())){
            throw new RuntimeException("商品状态必须是未审核的！");
        }

        //设置商品状态为审核通过
        Spu spuUpdate = new Spu();
        spuUpdate.setId(spuId);
        spuUpdate.setStatus("1");
        spuMapper.updateByPrimaryKeySelective(spuUpdate);

    }


    @Override
    public void upGoods(String spuId) {
        //1.查询spu商品
        Spu spu = spuMapper.selectByPrimaryKey(spuId);

        //2.前置条件判断
        if(spu==null){
            throw new RuntimeException("数据不存在！");
        }
        //商品的状态必须是审核通过的，才可以点击上架
        if(!"1".equals(spu.getStatus())){
            throw new RuntimeException("审核通过的商品才能上架！");
        }
        //商品如果已经上架了或者是其他的非未上架状态，那么都不允许上架
        if(!"0".equals(spu.getIsMarketable())){
            throw new RuntimeException("未上架的商品才能上架！");
        }

        //3.设置状态为已上架
        Spu spuUpdate = new Spu();
        spuUpdate.setId(spuId);
        spuUpdate.setIsMarketable("1");
        spuMapper.updateByPrimaryKeySelective(spuUpdate);


        //4.将spuId数据存放到商品上架的mq中
        rabbitTemplate.convertAndSend(Constants.GOODS_UP_EXCHANGE,"" ,spuId );
    }

    @Override
    public void downGoods(String spuId) {
        //1.根据spuId查询spu
        Spu spu = spuMapper.selectByPrimaryKey(spuId);

        //2.前置条件判断
        if(spu==null){
            throw new RuntimeException("数据不存在！");
        }
        //如果商品的状态不是已上架的，则抛异常
        if(!"1".equals(spu.getIsMarketable())){
            throw new RuntimeException("只有已上架的商品才能下架！");
        }

        //3.设置状态为已下架
        Spu spuUpdate = new Spu();
        spuUpdate.setId(spuId);
        spuUpdate.setIsMarketable("0");
        spuMapper.updateByPrimaryKeySelective(spuUpdate);

        //4.将spuId存入MQ中，MQ的消费者负责根据spuId将数据从ES中删除
        rabbitTemplate.convertAndSend(Constants.GOODS_DOWN_EXCHANGE, "", spuId);
    }


    @Override
    public void deleteGoodsLogic(String spuId) {
        //查询spu
        Spu spu = spuMapper.selectByPrimaryKey(spuId);

        //前置条件判断
        if(spu==null){
            throw new RuntimeException("数据为空");
        }
        //如果商品的状态不是下架的状态，那么抛异常
        if(!"0".equals(spu.getIsMarketable())){
            throw  new RuntimeException("已下架的商品才能删除");
        }

        //如果商品的状态不是未删除的状态，那么抛异常
        if(!"0".equals(spu.getIsDelete())){
            throw  new RuntimeException("只有未删除的商品才能删除");
        }

        //设置删除状态为已删除
        Spu spuUpdate = new Spu();
        spuUpdate.setId(spuId);
        spuUpdate.setIsDelete("1");
        spuMapper.updateByPrimaryKeySelective(spuUpdate);
    }


    @Override
    public void restoreGoods(String spuId) {
        //查询spu
        Spu spu = spuMapper.selectByPrimaryKey(spuId);

        //前置条件判断
        if(spu==null){
            throw new RuntimeException("数据为空");
        }

        //如果商品的状态不是未已删除的状态，那么抛异常
        if(!"1".equals(spu.getIsDelete())){
            throw  new RuntimeException("只有已经逻辑删除的商品才能恢复");
        }

        //设置删除状态为已删除
        Spu spuUpdate = new Spu();
        spuUpdate.setId(spuId);
        spuUpdate.setIsDelete("0");
        spuMapper.updateByPrimaryKeySelective(spuUpdate);
    }


    @Transactional
    @Override
    public void deleteGoods(String spuId) {
        //查询spu
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        //前置条件判断
        if(spu==null){
            throw new RuntimeException("数据为空");
        }
        //如果商品的状态不是下架的状态，那么抛异常
        if(!"0".equals(spu.getIsMarketable())){
            throw  new RuntimeException("已下架的商品才能删除");
        }

        //删除spu
        spuMapper.deleteByPrimaryKey(spuId);

        //删除sku
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        skuMapper.delete(sku);
    }
}
