package com.zhm.dto;

import java.util.List;

/**
 * Created by 赵红明 on 2019/8/23.
 */
public class AddressDto {

    private String label;
    private Integer value;
    private List<AddressDto> children;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public List<AddressDto> getChildren() {
        return children;
    }

    public void setChildren(List<AddressDto> children) {
        this.children = children;
    }
}
