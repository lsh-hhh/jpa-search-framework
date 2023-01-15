package com.lsh.dto;

import com.lsh.framework.BaseSearchDTO;
import com.lsh.framework.PageQueryParam;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CustomerSearchPageDTO extends BaseSearchDTO {

    @PageQueryParam(sqlKeyword = "id", type = PageQueryParam.QueryType.EQ)
    private Long id;

    @PageQueryParam(sqlKeyword = "id", type = PageQueryParam.QueryType.IN)
    private List<Long> ids;

    @PageQueryParam(sqlKeyword = "id", type = PageQueryParam.QueryType.IN)
    private Long[] idsArr;

    @PageQueryParam(sqlKeyword = "id", type = PageQueryParam.QueryType.IN)
    private String idsStr;

    @PageQueryParam(sqlKeyword = "wm_level", type = PageQueryParam.QueryType.EQ)
    private Integer wmLevel;

}
