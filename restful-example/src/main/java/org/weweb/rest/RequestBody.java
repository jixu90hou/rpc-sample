package org.weweb.rest;

public class RequestBody {
    private String key;
    private Long id;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "RequestBody{" + "key='" + key + '\'' + ", id=" + id + '}';
    }
}
