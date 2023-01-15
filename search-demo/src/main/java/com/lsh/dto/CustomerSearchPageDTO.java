package com.lsh.dto;

import com.lsh.framework.BaseSearchDTO;
import com.lsh.framework.PageQueryParam;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CustomerSearchPageDTO extends BaseSearchDTO {

    @PageQueryParam(sqlKeyword = "id", type = PageQueryParam.QueryType.EQUAL)
    private Long id;

    @PageQueryParam(sqlKeyword = "id", type = PageQueryParam.QueryType.IN)
    private List<Long> ids;

    @PageQueryParam(sqlKeyword = "wm_level", type = PageQueryParam.QueryType.EQUAL)
    private Integer wmLevel;

}
