package com.lsh.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class CustomerPageDTO {

    private Long id;

    private String mobile;

    private String nickname;

    private String headImg;

    private LocalDateTime bindContactTime;
}
