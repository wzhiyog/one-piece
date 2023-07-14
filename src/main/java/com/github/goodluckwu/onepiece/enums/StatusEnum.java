package com.github.goodluckwu.onepiece.enums;


public enum StatusEnum {
    INIT("I", "初始化"),
    PROCESSING("P", "处理中"),
    SUCCESS("S", "成功"),
    FAIL("F", "失败");

    private String code;
    private String desc;

    StatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    static {
        // 通过名称构建缓存,通过EnumCache.findByName(StatusEnum.class,"SUCCESS",null);调用能获取枚举
        EnumCache.registerByName(StatusEnum.class);
        // 通过code构建缓存,通过EnumCache.findByValue(StatusEnum.class,"S",null);调用能获取枚举
        EnumCache.registerByValue(StatusEnum.class, StatusEnum::getCode);
    }

    public static void main(String [] args){
        System.out.println(EnumCache.findByName(StatusEnum.class, "SUCCESS"));
        // 返回默认值StatusEnum.INIT
        System.out.println(EnumCache.findByName(StatusEnum.class, null, StatusEnum.INIT));
        // 返回默认值StatusEnum.INIT
        System.out.println(EnumCache.findByName(StatusEnum.class, "ERROR", StatusEnum.INIT));


        System.out.println(EnumCache.findByValue(StatusEnum.class, "S"));
        // 返回默认值StatusEnum.INIT
        System.out.println(EnumCache.findByValue(StatusEnum.class, null, StatusEnum.INIT));
        // 返回默认值StatusEnum.INIT
        System.out.println(EnumCache.findByValue(StatusEnum.class, "ERROR", StatusEnum.INIT));
    }
}