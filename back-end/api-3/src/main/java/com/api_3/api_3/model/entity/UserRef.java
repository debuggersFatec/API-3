package com.api_3.api_3.model.entity;

public class UserRef {
    private String uuid;
    private String name;
    private String img;

    public UserRef(String uuid, String name, String img) {
        this.uuid = uuid;
        this.name = name;
        this.img = img;
    }

    public UserRef(User user) {
        this.uuid = user.getUuid();
        this.name = user.getName();
        this.img = user.getImg();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "UserRef{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", img='" + img + '\'' +
                '}';
    }

}
