import Vue from 'vue';
import moment from "moment";
import http from '../axios/http';
import Cookies from 'js-cookie';
import i18n from "../lang";

/**
 * 通用filter
 */
// 空值过滤器
export function nullValFilter(value) {
    return (value === null || value === undefined) ? 'null' : value;
}
// 为空
export function isEmpty(value) {
    if (value === null || value === undefined) {
        return true;
    }
    if (value.length === 0) {
        return true;
    }
    return value instanceof String && value.trim().length === 0;
}
// 非空
export function isNotEmpty(value) {
    return !isEmpty(value);
}
// 空对象
export function isObjectEmpty(obj) {
    if(obj === null || obj === undefined) {
        return true;
    }
    if (obj.constructor !== Object) {
        return obj.length === 0;
    }
    return Object.keys(obj).length === 0;
}
// 是否为数字
export function isRealNum(val){
    // isNaN()函数 把空串 空格 以及NUll 按照0来处理 所以先去除
    if(val === "" || val ==null){
        return false;
    }
    if(!isNaN(val)){
        return true;
    }else{
        return false;
    }
}

// 日期-时间 格式化 YYYY
export function year(value) {
    if (value) {
        return moment(value).format("YYYY");
    }
    return '';
}
// 日期-时间 格式化 YYYY-MM
export function yearmonth(value) {
    if (value) {
        return moment(value).format("YYYY-MM");
    }
    return '';
}
// 日期格式化 YYYY-MM-DD
export function date(value) {
    if (value) {
        return moment(value).format("YYYY-MM-DD");
    }
    return '';
}
// 日期-时间 格式化 YYYY-MM-DD HH:mm:ss
export function datetime(value) {
    if (value) {
        return moment(value).format("YYYY-MM-DD HH:mm:ss");
    }
    return '';
}
//参数表code换name 传总单code过来
export function paramCode2ParamCname(value, paramCode) {
    // console.log("paramCode2ParamCname")
    // console.log(value)
    // console.log(paramCode)
    if (isEmpty(value)) {
        return null;
    }
    let url = "api/params/queryParamDetailDetails?code=" + paramCode;
    let listJson = window.sessionStorage.getItem(url);
    let list = listJson ? JSON.parse(listJson) : null;
    // console.log(list)

    if(list == null) {
        try {
            let resultList = http.sync_get(url);
            if(resultList && resultList.indexOf('[')===0 && resultList.length > 0) {
                window.sessionStorage.setItem(url, resultList);
            }
            // 第一次进来也要执行转化
            list = JSON.parse(resultList);
        } catch (e) {
            return null;
        }
    }

    //匹配
    let isMatch = false;
    let language = Cookies.get('language') || 'zh';
    if(list != null && list.constructor === Array) {
        for (let i = 0; i < list.length; i++) {
            // window.sessionStorage.setItem(value, list[i]['cname']);
            if (value == list[i]['code']) {
                isMatch = true;
                if(language == "zh"){
                    return list[i]['cname'];
                } else {
                    return list[i]['ename'];
                }
            }
        }
        if(!isMatch){
            return value
        }
    }
    return null;
}

//根据菜单id查询查单名称
export function deptId2DeptName(value) {
    // console.log("customerService2Name")
    // console.log(value)
    if (isEmpty(value)) {
        return null;
    }
    let url = "api/sysDept/listName/" + value;
    let data = window.sessionStorage.getItem(url);
    if (data) {
        return data
    }
    try {
        let result = http.sync_get(url);
        if (result && result.length > 0) {
            window.sessionStorage.setItem(url, result);
            return result
        } else {
            return value;
        }
    } catch (e) {
        return value;
    }
}
