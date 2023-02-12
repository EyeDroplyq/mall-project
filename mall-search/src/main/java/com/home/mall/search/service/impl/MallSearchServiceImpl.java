package com.home.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.home.common.to.es.SkuEsModel;
import com.home.common.utils.R;
import com.home.mall.search.config.ElasticSearchConfig;
import com.home.mall.search.constants.EsConstant;
import com.home.mall.search.feign.ProductFeignService;
import com.home.mall.search.service.MallSearchService;
import com.home.mall.search.vo.AttrRespVo;
import com.home.mall.search.vo.SearchParamVo;
import com.home.mall.search.vo.SearchResult;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.swing.text.Highlighter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: lyq
 * @createDate: 29/1/2023
 * @version: 1.0
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {
    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private ProductFeignService productFeignService;

    /**
     * 通过前端传过来的条件进行检索
     *
     * @param searchParamVo
     * @return
     */
    @Override
    public SearchResult search(SearchParamVo searchParamVo) {
        SearchResult result = null;//最终返回的结果
        SearchRequest searchRequest = buildSearchRequest(searchParamVo);
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
            result = buildSearchResult(search, searchParamVo);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 构建用来DSL的请求
     *
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParamVo searchParamVo) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //构建复杂的DSL语句

        //查询部分
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (!StringUtils.isEmpty(searchParamVo.getKeyword())) {
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", searchParamVo.getKeyword()));
        }

        if (searchParamVo.getCatagory3Id() != null) {
            boolQuery.filter(QueryBuilders.termQuery("catalogId", searchParamVo.getCatagory3Id()));
        }

        if (searchParamVo.getBrandId() != null && searchParamVo.getBrandId().size() > 0) {
            boolQuery.filter(QueryBuilders.termsQuery("brandId", searchParamVo.getBrandId()));
        }

        if (searchParamVo.getHasStock() != null) {
            boolQuery.filter(QueryBuilders.termsQuery("hasStock", searchParamVo.getHasStock() == 1));
        }

        if (!StringUtils.isEmpty(searchParamVo.getSkuPrice())) {
            //价格区间的格式是：_500,1_1000,1000_
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            String[] s = searchParamVo.getSkuPrice().split("_");
            if (s.length == 2) {
                //1_1000格式
                rangeQueryBuilder.gte(s[0]).lte(s[1]);
            } else if (s.length == 1) {
                if (searchParamVo.getSkuPrice().startsWith("_")) {
                    //_500格式
                    rangeQueryBuilder.lte(s[0]);
                }
                if (searchParamVo.getSkuPrice().endsWith("_")) {
                    //1000_格式
                    rangeQueryBuilder.gte(s[0]);
                }
            }
            boolQuery.filter(rangeQueryBuilder);
        }

        if (searchParamVo.getAttrs() != null && searchParamVo.getAttrs().size() > 0) {
            for (String attrStr : searchParamVo.getAttrs()) {
                //属性的格式为attrs=1_5寸:8寸&attrs=2_4K:1080P
                BoolQueryBuilder nestedBoolQueryBuilder = QueryBuilders.boolQuery();
                String[] s = attrStr.split("_");
                String attrId = s[0];//attrId
                String[] attrValues = s[1].split(":");
                nestedBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId", attrId));//属性值id为这个id的
                nestedBoolQueryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));//属性值列表
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", nestedBoolQueryBuilder, ScoreMode.None);
                boolQuery.filter(nestedQueryBuilder);
            }
        }
        sourceBuilder.query(boolQuery);


        //排序部分
        if (!StringUtils.isEmpty(searchParamVo.getSort())) {
            //排序的格式是sort=saleCount_asc sort=saleCount_desc
            String[] s = searchParamVo.getSort().split("_");
            SortOrder order = (s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC);
            sourceBuilder.sort(s[0], order);
        }

        //分页部分
        //不用判空，因为默认就是从第一页开始
        //显示指定页的数据的算法： （pageNum-1）*pageSize
        sourceBuilder.from((int) ((searchParamVo.getPageNum() - 1) * EsConstant.PAGE_SIZE));
        sourceBuilder.size(Math.toIntExact(EsConstant.PAGE_SIZE));

        //高亮部分，只有模糊查询的时候才进行高亮显示
        if (!StringUtils.isEmpty(searchParamVo.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }

        //聚合部分
        //1.品牌聚合
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_agg");
        brandAgg.field("brandId");
        brandAgg.size(50);
        //品牌聚合下的子聚合
        brandAgg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brandAgg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(50));
        sourceBuilder.aggregation(brandAgg);

        //2.分类聚合
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalog_agg");
        catalogAgg.field("catalogId").size(50);
        //分类下的子聚合
        catalogAgg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(50));
        sourceBuilder.aggregation(catalogAgg);

        //属性聚合(嵌入聚合)
        NestedAggregationBuilder nested = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId").size(50);
        //attrIdAgg聚合下还有子聚合
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(10));
        nested.subAggregation(attr_id_agg);
        sourceBuilder.aggregation(nested);

        System.out.println(sourceBuilder.toString());

        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
        return searchRequest;

    }

    /**
     * 构建最终返回结果
     *
     * @param search
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse search, SearchParamVo searchParamVo) {
        SearchResult result = new SearchResult();

        //        //1.封装所有的商品
        SearchHits hits = search.getHits();
        List<SkuEsModel> products = new ArrayList<>();
        for (SearchHit hit : hits.getHits()) {
            //所有命中数据
            SkuEsModel esModel = new SkuEsModel();
            String sourceAsString = hit.getSourceAsString();
            esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
            if (!StringUtils.isEmpty(searchParamVo.getKeyword())) {
                HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                String title = skuTitle.getFragments()[0].toString();
                esModel.setSkuTitle(title);
            }
            products.add(esModel);

        }
        result.setProducts(products);

        //===========以下的数据来自聚合的数据，不是来自命中的数据

        //2.设置属性信息
        List<SearchResult.AttrVo> attrs = new ArrayList<>();
        ParsedNested attrAgg = search.getAggregations().get("attr_agg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attr_name_agg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attr_value_agg");
            List<String> attrValues = attrValueAgg.getBuckets().stream().map(item -> {
                return item.getKeyAsString();
            }).collect(Collectors.toList());
            attrVo.setAttrValue(attrValues);
            attrs.add(attrVo);
        }
        result.setAttrs(attrs);

//        result.setAttrs();
        //3.设置品牌信息
        ParsedLongTerms brandAgg = search.getAggregations().get("brand_agg");
        List<SearchResult.BrandVo> brands = new ArrayList<>();
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            long brandId = bucket.getKeyAsNumber().longValue();
            brandVo.setBrandId(brandId);
            ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brand_img_agg");
            String brandImg = brandImgAgg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brandImg);
            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brand_name_agg");
            String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);
            brands.add(brandVo);
        }
        result.setBrands(brands);

//        result.setBrands();
        //4.设置分类信息
        ParsedLongTerms catalog_agg = search.getAggregations().get("catalog_agg"); //如果不用ParsedLongTerms得不到bucket中的信息
        List<SearchResult.CatalogVo> catalogs = new ArrayList<>();
        List<? extends Terms.Bucket> buckets = catalog_agg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            long catalogId = bucket.getKeyAsNumber().longValue();
            catalogVo.setCatalogId(catalogId);
            ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalog_name_agg");
            String catalogName = catalogNameAgg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalogName);
            catalogs.add(catalogVo);
        }
        result.setCatalogs(catalogs);


        //=====================以下是分页数据
        //5.设置当前页
        result.setPageNum(searchParamVo.getPageNum());
        //6.设置总记录数
        Long total = hits.getTotalHits().value;
        result.setTotal(total);
        //7.设置总页数
        Integer totalPages = Math.toIntExact(total % EsConstant.PAGE_SIZE == 0 ? (total / EsConstant.PAGE_SIZE) : (total / EsConstant.PAGE_SIZE + 1));
        result.setTotalPages(totalPages);
        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);



        if (searchParamVo.getAttrs() != null && searchParamVo.getAttrs().size() > 0) {
            //封装面包屑的信息
            List<SearchResult.NavVo> navVos = searchParamVo.getAttrs().stream().map(attr -> {
                //属性格式attrs=1_A15:A14
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                R r = productFeignService.info(Long.parseLong(s[0]));
                result.getAttrIds().add(Long.parseLong(s[0]));
                if (r.getCode() == 0) {
                    //远程调用成功
                    AttrRespVo attrRespVo = r.getData("attr", new TypeReference<AttrRespVo>() {
                    });
                    navVo.setNavName(attrRespVo.getAttrName());
                } else {
                    navVo.setNavName(s[0]);
                }
                //封装如果删除面包屑跳转到哪个地方
                String queryString = searchParamVo.getQueryString(); //得到查询语句
                //因为中文编码问题以及在java中空格对应url中+对应的前段是%20,所以我们需要进行url编码
                String encode=null;
                try {
                    encode= URLEncoder.encode(attr, "UTF-8");
                    encode = encode.replace("+", "%20");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String replace = queryString.replace("&attrs=" + encode, "");
                navVo.setLink("http://search.mall.com/list.html?"+replace);
                return navVo;
            }).collect(Collectors.toList());
            result.setNavs(navVos);
        }


        return result;
    }
}
