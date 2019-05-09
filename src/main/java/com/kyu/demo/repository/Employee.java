package com.kyu.demo.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Project : spring_boot_webflux
 * @Date : 2019-05-09
 * @Author : nklee
 * @Description :
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    private String id;
    private String name;
}