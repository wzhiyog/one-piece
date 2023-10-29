package com.github.wuzhiyong7.onepiece.infrastration.exception;

import java.util.Optional;
import java.util.UUID;

/**
 * 异常基类
 * @author wuzhihao-jk
 */
public abstract class BaseException extends RuntimeException {


    /**
     * 将一个对象的hashCode转成6位36进制字符串，统一转大写，并替换某些容易引起视觉歧义的字符。hashCode有可能是负数，结果会带负号，将负号替换位空并去除结果前后空白
     * @param obj 某个对象
     * @return 36进制字符串
     */
    public static String toBase36String(Object obj) {
        String str = Optional.ofNullable(obj).orElse(UUID.randomUUID()).toString();
        if(str.trim().isEmpty()){
            str = UUID.randomUUID().toString();
        }
        return Integer.toString(str.hashCode(), Character.MAX_RADIX).toUpperCase()
            .replace(' ', '0')
            .replace('O', '0')
            .replace('I', '1')
            .replace('-', ' ')
            .trim()
            ;
    }
}
