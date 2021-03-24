package com.yanmade.plat.framework.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CommonUtil {

    private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);
    private static final String UID = "serialVersionUID";

    public Map<String, Object> convertObjectToMap(Object object) {
        Map<String, Object> map = new HashMap<>();
        
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (UID.equals(field.getName())) {
                continue;
            }

            field.setAccessible(true);
            String name = field.getName();
            String upperName = name.substring(0, 1).toUpperCase() + name.substring(1);
            try {
                Method method = object.getClass().getMethod("get" + upperName);
                Object value = method.invoke(object);

                map.put(name, value);
            } catch (Exception e) {
                logger.error("反射获取属性的值错误：", e);
            }
        }

        return map;
    }

}
