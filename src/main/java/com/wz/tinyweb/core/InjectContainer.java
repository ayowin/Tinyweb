package com.wz.tinyweb.core;

import java.util.HashMap;

public class InjectContainer {

    private static HashMap<String,Object> objectHashMap = new HashMap<>();

    private static class NullKeyException extends Throwable{}
    private static class NullObjectException extends Throwable{}
    private static class ExistKeyException extends Throwable{
        public ExistKeyException(String content){
            super(content);
        }
    }

    public static void add(String key,Object object){
        try{
            if(key == null){
                throw new NullKeyException();
            } else if(object == null){
                throw new NullObjectException();
            } else if(objectHashMap.containsKey(key)){
                throw new ExistKeyException(String.format("key '%s' is exist",key));
            } else {
                objectHashMap.put(key,object);
            }
        } catch (NullKeyException | NullObjectException | ExistKeyException e) {
            e.printStackTrace();
        }
    }

    public static Object get(String key){
        Object object = null;
        if(key != null){
            object = objectHashMap.get(key);
        }
        return object;
    }

    public static void remove(String key){
        if(key != null){
            objectHashMap.remove(key);
        }
    }
}
