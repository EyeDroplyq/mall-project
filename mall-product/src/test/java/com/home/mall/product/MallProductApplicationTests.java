package com.home.mall.product;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.home.mall.product.dao.AttrGroupDao;
import com.home.mall.product.dao.SkuSaleAttrValueDao;
import com.home.mall.product.entity.BrandEntity;
import com.home.mall.product.service.BrandService;
import com.home.mall.product.service.CategoryService;
import com.home.mall.product.vo.SkuItemVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MallProductApplicationTests {

    @Autowired
    public BrandService brandService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private AttrGroupDao groupDao;
    @Resource
    private SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Test
    public void dao(){
        List<SkuItemVo.SpuItemAttrGroupVo> attrAndAttrGroupByCatalogIdAndSpuId = groupDao.getAttrAndAttrGroupByCatalogIdAndSpuId(225L, 1L);
        System.out.println(attrAndAttrGroupByCatalogIdAndSpuId);
    }

    @Test
    public void saleDao(){
        List<SkuItemVo.SkuItemSaleAttrVo> spuSaleAttrBySpuId = skuSaleAttrValueDao.getSpuSaleAttrBySpuId(1L);
        System.out.println(spuSaleAttrBySpuId);
    }


    @Test
    public void redisson(){
        System.out.println(redissonClient);
    }

    @Test
    public void contextLoads() {
//        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setName("华为");
//        brandService.save(brandEntity);
//        System.out.println("保存成功");
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("brand_id", 1);
        List<BrandEntity> list = brandService.list(queryWrapper);
        for (BrandEntity item : list) {
            System.out.println(item);
        }
    }

    @Test
    public void test(){
        Long[] path = categoryService.findCatelogPath(225L);
        log.info("path:", Arrays.asList(path));

    }

    @Test
    public void test02(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("hello","world"+ UUID.randomUUID().toString());
        String hello = ops.get("hello");
        System.out.println("保存的数据是： "+hello);

    }

}
