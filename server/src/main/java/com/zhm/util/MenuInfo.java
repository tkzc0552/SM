package com.zhm.util;


import java.util.List;

/**
 * Created by 赵红明 on 2019/8/22.
 */
public class MenuInfo {
    private String parentMenuCode;
    private String menuCode;
    private String menuEname;
    private String menuCname;
    private Integer leafFlag;
    private String menuLink;
    private Integer menuLevel;
    List<MenuInfo> children;
    List<ButtonInfo> opts;

    public String getParentMenuCode() {
        return parentMenuCode;
    }

    public void setParentMenuCode(String parentMenuCode) {
        this.parentMenuCode = parentMenuCode;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public String getMenuEname() {
        return menuEname;
    }

    public void setMenuEname(String menuEname) {
        this.menuEname = menuEname;
    }

    public String getMenuCname() {
        return menuCname;
    }

    public void setMenuCname(String menuCname) {
        this.menuCname = menuCname;
    }

    public Integer getLeafFlag() {
        return leafFlag;
    }

    public void setLeafFlag(Integer leafFlag) {
        this.leafFlag = leafFlag;
    }

    public String getMenuLink() {
        return menuLink;
    }

    public void setMenuLink(String menuLink) {
        this.menuLink = menuLink;
    }

    public Integer getMenuLevel() {
        return menuLevel;
    }

    public void setMenuLevel(Integer menuLevel) {
        this.menuLevel = menuLevel;
    }

    public List<MenuInfo> getChildren() {
        return children;
    }

    public void setChildren(List<MenuInfo> children) {
        this.children = children;
    }

    public List<ButtonInfo> getOpts() {
        return opts;
    }

    public void setOpts(List<ButtonInfo> opts) {
        this.opts = opts;
    }
}
