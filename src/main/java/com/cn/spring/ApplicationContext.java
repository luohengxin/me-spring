package com.cn.spring;

import com.cn.spring.annotation.Autowired;
import com.cn.spring.annotation.Component;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

public class ApplicationContext {

    private static ResourceBundle properties = ResourceBundle.getBundle("application");


    private Map<String, Object> cache = new HashMap<>();
    private List<String> classNames = new ArrayList<>();


    public ApplicationContext() {
        refresh();
    }

    public void refresh(){
        // 定位需要加载的class
        doScanner(properties.getString("basePackage"));

        doInstances();

        doAutowired();


    }

    private void doAutowired() {
        cache.forEach((key ,obj) -> {
            Field[] fields = obj.getClass().getDeclaredFields();
            for(Field field :fields){
                Autowired autowired = field.getAnnotation(Autowired.class);
                if (autowired != null ){
                    Object value = getObject(field.getName());
                    try {
                        field.setAccessible(true);
                        field.set(obj,value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void doInstances() {
        if(classNames.size() == 0){
            return;
        }
        try {
            for(String className: classNames){

                Class<?> aClass = Class.forName(className);
                if(needInstance(aClass)){
                    Object o = aClass.newInstance();
                    String beanName = getBeanName(aClass);
                     if(cache.containsKey(beanName)){
                        throw new RuntimeException("有两个相同的名字");
                    }
                    cache.put(beanName,o);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getBeanName(Class<?> aClass) {
        Component annotation = aClass.getAnnotation(Component.class);
        String simpleName = aClass.getSimpleName();
        String beanName = simpleName.substring(0, 1).toLowerCase().concat(simpleName.substring(1));
        if(annotation != null && !annotation.value().equals("")){
            beanName = annotation.value();
        }
        return beanName;
    }

    private boolean needInstance(Class<?> aClass) {
        Component annotation = aClass.getAnnotation(Component.class);
        if(annotation != null){
            return true;
        }
        return false;
    }

    private void doScanner(String basePackage) {
        URL resource = this.getClass().getResource("/" + basePackage.replace(".", "/"));
        File file = new File(resource.getFile());
        for(File classFile :file.listFiles()){
            if(classFile.isDirectory()){
                doScanner(basePackage+"."+classFile.getName());
            }else{
                if(!classFile.getName().endsWith(".class")){
                    continue;
                }
                String className = basePackage + "." + classFile.getName().replace(".class","");
                classNames.add(className);
            }
        }
    }
    public <T> T getObject(String beanName){
        return (T)cache.get(beanName);
    }
}
