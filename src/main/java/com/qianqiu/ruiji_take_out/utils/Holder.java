package com.qianqiu.ruiji_take_out.utils;

import com.qianqiu.ruiji_take_out.pojo.Employee;
import com.qianqiu.ruiji_take_out.pojo.User;

/**
 * 基于ThreadLocal封装工具类，保存用户
 */
public class Holder {
    private static final ThreadLocal<User> tl = new ThreadLocal<>();
    private static final ThreadLocal<Long> tlId = new ThreadLocal<>();
    private static final ThreadLocal<Long> tlId2 = new ThreadLocal<>();

    public static void saveUser(User user){
        tl.set(user);
    }
    public static void saveId(Long id){
        tlId.set(id);
    }
    public static void saveId2(Long id){
        tlId2.set(id);
    }

    public static User getUser(){
        return tl.get();
    }
    public static Long getId(){
        return tlId.get();
    }
    public static Long getId2(){
        return tlId2.get();
    }

    public static void removeUser(){
        tl.remove();
    }
    public static void removeId(){
        tlId.remove();
    }
    public static void removeId2(){
        tlId2.remove();
    }
}