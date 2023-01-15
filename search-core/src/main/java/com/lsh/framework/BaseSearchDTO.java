package com.lsh.framework;

import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

@Data
public class BaseSearchDTO {

    private Integer page = 1;
    private Integer pageSize = 10;

    private String sortBy;

    private boolean sortDesc;

    @JsonIgnore
    public String getFixedWhere() {
        return "1=1";
    }

    @JsonIgnore
    public String getDefaultOrder() {
        return "order by id desc";
    }
}
