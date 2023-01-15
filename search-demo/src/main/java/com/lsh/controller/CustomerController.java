package com.lsh.controller;

import com.lsh.common.Result;
import com.lsh.dto.CustomerPageDTO;
import com.lsh.dto.CustomerSearchPageDTO;
import com.lsh.framework.PageDTO;
import com.lsh.repository.CustomerPageRepository;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Resource
    CustomerPageRepository customerPageRepository;

    @GetMapping("/list")
    public Result<PageDTO<CustomerPageDTO>> search(CustomerSearchPageDTO searchPageDTO) {
        return Result.success(customerPageRepository.search(searchPageDTO, CustomerPageDTO.class));
    }

    @GetMapping("/count")
    public Result<Long> count(CustomerSearchPageDTO searchPageDTO) {
        return Result.success(customerPageRepository.count(searchPageDTO));
    }
}
