package com.home.mall.search;


import com.alibaba.fastjson.JSON;
import com.home.mall.search.config.ElasticSearchConfig;
import jdk.nashorn.internal.parser.JSONParser;
import lombok.Data;
import lombok.experimental.Accessors;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContent;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.Index;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.swing.*;
import java.io.IOException;

@RunWith(SpringRunner.class)

@SpringBootTest
public class MallSearchApplicationTests {
    @Resource
    public RestHighLevelClient client;

    @Test
    public void contextLoads() {
        System.out.println(client);
    }

    /**
     *  保存索引以及文档
     * @throws IOException
     */
    @Test
    public void test01() throws IOException {
        User user = new User();
        user.setName("zhangsan").setGender("男").setAge(18);
        String s = JSON.toJSONString(user);
        IndexRequest indexRequest=new IndexRequest("users");
        indexRequest.id("1");
        indexRequest.source(s, XContentType.JSON);
        client.index(indexRequest, ElasticSearchConfig.COMMON_OPTIONS);

    }

    @Data
    //开启链式set
    @Accessors(chain = true)
    class User {
        public String name;
        public String gender;
        public Integer age;

        public User() {
        }

        public User(String name, String gender, Integer age) {
            this.name = name;
            this.gender = gender;
            this.age = age;
        }

    }

    @Test
    public void test02() throws IOException {

    }


}
