package com.zh.physiology.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * author：heng.zhang
 * date：2017/2/21
 * description：用户信息表
 */
@Table(name = "user")
public class UserEntity {
    @Column(name="id", isId=true, autoGen=true)
    private int id;

    @Column(name="name")
    private String name;

    @Column(name="num")
    private String num;

    private static UserEntity userEntity;

    public static UserEntity getInstance() {
        if(userEntity == null) {
            userEntity = new UserEntity();
        }

        return userEntity;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
