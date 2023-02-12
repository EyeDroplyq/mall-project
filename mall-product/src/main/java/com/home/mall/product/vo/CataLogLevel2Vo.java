package com.home.mall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: 二级分类vo
 * @author: lyq
 * @createDate: 25/1/2023
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CataLogLevel2Vo {
    private String catalog1Id;
    private List<CataLogLevel3Vo> catalog3List;
    private String id;
    private String name;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CataLogLevel3Vo{
        private String catalog2Id;
        private String id;
        private String name;
    }
}
