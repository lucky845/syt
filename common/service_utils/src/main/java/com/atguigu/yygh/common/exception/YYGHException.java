package com.atguigu.yygh.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YYGHException extends RuntimeException{

    private Integer code;
    private String message;

}
