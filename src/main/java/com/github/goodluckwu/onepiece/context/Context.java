package com.github.goodluckwu.onepiece.context;

import java.util.HashMap;
import java.util.Map;

public class Context {
    private static final InheritableThreadLocal<Map<String, Object>> context = new InheritableThreadLocal<>(){
        @Override
        protected Map<String, Object> initialValue() {
            return new HashMap<>();
        }
    };

    public static void put(String key, String value){
        context.get().put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key){
        return (T) context.get().get(key);
    }

    public static void remove(String key){
        context.get().remove(key);
    }

    public static void clear(){
        context.get().clear();
        context.remove();
    }
}
