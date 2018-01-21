package org.weweb.rest.entity;

import org.msgpack.MessagePack;
import org.msgpack.annotation.Message;

import java.io.IOException;
import java.io.Serializable;

@Message
public class UserInfo implements Serializable{
    public UserInfo(){}
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserInfo{" + "id=" + id + ", name='" + name + '\'' + '}';
    }

    public static void main(String[] args) throws IOException {
        UserInfo userInfo=new UserInfo();
        userInfo.setId(22L);
        userInfo.setName("zhang22");
        MessagePack messagePack=new MessagePack();
        byte[] bytes=messagePack.write(userInfo);
        UserInfo userInfo1=messagePack.read(bytes,UserInfo.class);
        System.out.println(userInfo1);
    }
}
