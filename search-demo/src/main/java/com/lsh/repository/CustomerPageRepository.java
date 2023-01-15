package com.lsh.repository;

import com.lsh.dto.CustomerPageDTO;
import com.lsh.dto.CustomerSearchPageDTO;
import com.lsh.framework.BaseSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerPageRepository extends BaseSearchRepository<CustomerPageDTO, CustomerSearchPageDTO> {
    @Override
    protected String countSql() {
        return "select count(*) from customer";
    }

    @Override
    protected String selectSql() {
        return "select * from customer";
    }
}
