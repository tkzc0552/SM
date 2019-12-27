package com.zhm.dto;

import java.util.List;

/**
 * Created by 赵红明 on 2019/8/1.
 */
public class DeptDto {
    private Integer id;
    private String name;
    private String parentName;
    private Integer level;
    List<DeptDto> children;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<DeptDto> getChildren() {
        return children;
    }

    public void setChildren(List<DeptDto> children) {
        this.children = children;
    }
}
