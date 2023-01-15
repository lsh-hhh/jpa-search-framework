package com.lsh.controller;

import com.lsh.dto.CustomerPageDTO;
import com.lsh.dto.CustomerSearchPageDTO;
import com.lsh.framework.PageDTO;
import com.lsh.repository.CustomerPageRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Resource
    CustomerPageRepository customerPageRepository;

    @PostMapping("/list")
    public PageDTO<CustomerPageDTO> search(@RequestBody CustomerSearchPageDTO searchPageDTO) {
        return customerPageRepository.search(searchPageDTO, CustomerPageDTO.class);
    }
}
