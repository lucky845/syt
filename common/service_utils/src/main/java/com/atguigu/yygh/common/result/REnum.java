package com.atguigu.yygh.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum REnum {

    SUCCESS(20000, "成功", true),
    ERROR(20001, "失败", false);

    private final Integer code;
    private final String message;
    private final Boolean success;

}
