package com.home.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.home.common.to.es.SkuEsModel;
import com.home.mall.search.config.ElasticSearchConfig;
import com.home.mall.search.constants.EsConstant;
import com.home.mall.search.service.SkuSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: lyq
 * @createDate: 24/1/2023
 * @version: 1.0
 */
@Slf4j
@Service
public class SkuSaveServiceImpl implements SkuSaveService {
    @Resource
    private RestHighLevelClient restHighLevelClient;

    /**
     * 保存数据到es中
     *
     * @param esModels
     */
    @Override
    public void skuSave(List<SkuEsModel> esModels) throws IOException {
        //BulkRequest bulkRequest, RequestOptions options
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel esModel : esModels) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(esModel.getSkuId().toString());
            String s = JSON.toJSONString(esModel);
            indexRequest.source(s, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);

        boolean b = bulk.hasFailures();
        if(!b){
            //如果没有错误
            List<String> Ids = Arrays.stream(bulk.getItems()).map(item -> {
                return item.getId();
            }).collect(Collectors.toList());
            log.info("保存到es成功",Ids);
        }else{
            log.error("保存到es失败");
        }

    }

}
