package com.home.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.home.mall.product.entity.CategoryBrandRelationEntity;
import com.home.mall.product.service.CategoryBrandRelationService;
import com.home.mall.product.vo.CataLogLevel2Vo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.home.common.utils.PageUtils;
import com.home.common.utils.Query;

import com.home.mall.product.dao.CategoryDao;
import com.home.mall.product.entity.CategoryEntity;
import com.home.mall.product.service.CategoryService;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Resource
    private CategoryDao categoryDao;
    @Resource
    private CategoryBrandRelationService categoryBrandRelationService;
    @Resource
    private StringRedisTemplate stringsRedisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1.查询所有的分类数据
        List<CategoryEntity> entities = categoryDao.selectList(null);

        //2.查询每个分类的子分类，并把他们封装成一个树形结构
        /**
         * java8之后的新特性，函数式编程，先对这个返回的列表中的数据进行一个过滤，然后对这个过滤之后的数据进行一个收集，并且在过滤的时候我们可以再添加一个规则，和gateway有点类似
         */
        /**
         * 1.找到所有的一级分类
         */
        List<CategoryEntity> level1Menus = entities.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentCid() == 0;
        }).map((menu) -> { //添加映射规则
            menu.setChildren(getChildren(menu, entities)); //给当前这个一级菜单设置子菜单
            return menu;
        }).sorted( //进行排序，对不同的产品种类可以进行排序显示
                (menu1, menu2) -> {
                    return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                }
        ).collect(Collectors.toList());

        return entities;
    }

    /***
     * 批量删除功能
     * @param ids
     */
    @Override
    public void removeMenuByIds(List<Long> ids) {
        //TODO 1.判断这个类别是不是在别处被引用
        //2.进行批量逻辑删除
        categoryDao.deleteBatchIds(ids);
    }

    /**
     * 根据catelogid返回完整的三级菜单的catelogid数组
     * @param catlogId
     * @return
     */
    @Override
    public Long[] findCatelogPath(Long catlogId) {
        List<Long> paths=new ArrayList<>();
        List<Long> parentsPaths=findParentPath(catlogId,paths);
        Collections.reverse(parentsPaths);
        return parentsPaths.toArray(new Long[parentsPaths.size()]);
    }

    /**
     * 如果分类名字该表，也需要更新品牌分类关联表中的数据
     * @param category
     */
    @CacheEvict(value = "category",allEntries = true) //失效模式
    @Override
    public void updateCascade(CategoryEntity category) {
        //更新自己
        this.updateById(category);
        if(!StringUtils.isEmpty(category.getName())){ //如果分类名称改变，需要更新品牌分类关联表
            categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
        }
    }

    /**
     * 查询第一级分类
     * @return
     */
    @Cacheable(value = {"category"},key = "#root.methodName",sync = true)
    @Override
    public List<CategoryEntity> getFirstCategory() {
        List<CategoryEntity> categoryEntities = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

//    @Cacheable(value = {"category"},key = "#root.methodName")
//    @Override
//    public Map<String, List<CataLogLevel2Vo>> cataLogJson(){
//            Map<String, List<CataLogLevel2Vo>> cataLogJsonFromDb = cataLogJsonFromDb(); //查询数据库
//            return cataLogJsonFromDb;
//    }

    @Override
    public Map<String, List<CataLogLevel2Vo>> cataLogJson(){
        String cataLogJson = stringsRedisTemplate.opsForValue().get("cataLogJson");
        if(StringUtils.isEmpty(cataLogJson)){
            //如果redis中的没有对应的缓存
            Map<String, List<CataLogLevel2Vo>> cataLogJsonFromDb = cataLogJsonFromDb(); //查询数据库
            //保存到redis中
//            String s = JSON.toJSONString(cataLogJsonFromDb);
//            stringsRedisTemplate.opsForValue().set("cataLogJson",s, ThreadLocalRandom.current().nextInt(), TimeUnit.DAYS);//设置随机的过期时间
            return cataLogJsonFromDb;
        }
        //如果redis中有对应的缓存
        //转成对应的对象
        Map<String, List<CataLogLevel2Vo>> result = JSON.parseObject(cataLogJson, new TypeReference<Map<String, List<CataLogLevel2Vo>>>() {
        });
        return result;
    }

    /**
     * 返回首页中需要的三级分类的数据
     * @return
     */

    public Map<String, List<CataLogLevel2Vo>> cataLogJsonFromDb() {
        //将整个线程实例加锁
        synchronized (this){
            //抢到锁的线程进来之后第一件事情就是先查询redis中是不是真的不存在缓存
            String cataLogJson = stringsRedisTemplate.opsForValue().get("cataLogJson");
            if(!StringUtils.isEmpty(cataLogJson)){ //如果存在缓存,直接将结果返回
                Map<String, List<CataLogLevel2Vo>> result = JSON.parseObject(cataLogJson, new TypeReference<Map<String, List<CataLogLevel2Vo>>>() {
                });
                return result;
            }
            Map<String, List<CataLogLevel2Vo>> collect=null;
            //得到所有的第一级分类
            List<CategoryEntity> categoryLevel1 = this.getFirstCategory();

            if(categoryLevel1!=null){
                collect = categoryLevel1.stream().collect(Collectors.toMap(k -> {
                    return k.getCatId().toString();
                }, v -> { //v对应的是一级分类
                    //value应该放的是List<CataLogLevel2Vo>
                    //查询所有的这个一级分类下对应的二级分类
                    List<CategoryEntity> categoryLevel2 = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
                    List<CataLogLevel2Vo> level2Vos = null;
                    if (categoryLevel2 != null) {
                        level2Vos = categoryLevel2.stream().map(l2 -> {
                            CataLogLevel2Vo cataLogLevel2Vo = new CataLogLevel2Vo();
                            cataLogLevel2Vo.setId(l2.getCatId().toString());
                            cataLogLevel2Vo.setName(l2.getName());
                            cataLogLevel2Vo.setCatalog1Id(v.getCatId().toString());
                            cataLogLevel2Vo.setCatalog3List(null);
                            //查询这个二级分类下对应的所有的三级分类
                            List<CategoryEntity> categoryLevel3 = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", l2.getCatId()));
                            if (categoryLevel3 != null) {
                                List<CataLogLevel2Vo.CataLogLevel3Vo> cataLogLevel3Vos = categoryLevel3.stream().map(l3 -> {
                                    CataLogLevel2Vo.CataLogLevel3Vo cataLogLevel3Vo = new CataLogLevel2Vo.CataLogLevel3Vo();
                                    cataLogLevel3Vo.setCatalog2Id(l2.getCatId().toString());
                                    cataLogLevel3Vo.setName(l3.getName());
                                    cataLogLevel3Vo.setId(l3.getCatId().toString());
                                    return cataLogLevel3Vo;
                                }).collect(Collectors.toList());
                                cataLogLevel2Vo.setCatalog3List(cataLogLevel3Vos);
                            }
                            return cataLogLevel2Vo;
                        }).collect(Collectors.toList());
                    }
                    return level2Vos;
                }));
            }
            String s = JSON.toJSONString(collect);
            stringsRedisTemplate.opsForValue().set("cataLogJson",s, 1, TimeUnit.DAYS);//设置随机的过期时间
            return collect;
        }

    }

    private List<Long> findParentPath(Long groupId, List<Long> paths) {
        paths.add(groupId);
        CategoryEntity categoryEntity = this.getById(groupId);
        if(categoryEntity.getParentCid()!=0){ //如果有父菜单的话
            //递归调用该方法，找到父节点的路径
            findParentPath(categoryEntity.getParentCid(),paths);
        }
        return paths;
    }

    /**
     * 递归得到当前菜单的所有子菜单
     *
     * @param root:当前进行分类的菜单
     * @param all:所有的菜单
     * @return: 返回当前菜单的所有子菜单
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentCid() == root.getCatId();//当前这个类别的父id等于当前根类别的id
        }).map(//添加映射条件
                (categoryEntity) -> {
                    categoryEntity.setChildren(getChildren(categoryEntity, all)); //递归调用这个方法，找它的子类
                    return categoryEntity;
                }
        ).sorted(//进行排序
                (menu1, menu2) -> {
                    return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                }
        ).collect(Collectors.toList());
        return children;
    }

}