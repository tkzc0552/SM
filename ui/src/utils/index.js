/**
 * Created by lixianzhang on 2017/7/17.
 */
import http from "../axios/http";
import moment from "moment";
import i18n from '../lang/index';

const utils = {
    install(Vue) {
        Vue.prototype.utils = {
            /* 公共方法 */
            // 计算表格高度
            calcTableHeight() {
                // console.log("calcTableHeight");
                // 窗口 高度
                let clientHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
                // console.log("clientHeight:" + clientHeight);

                // toolbar 高度
                let toolBarHeight = 0;
                let toolbarNum = document.getElementsByClassName("toolbar").length;
                if (toolbarNum && toolbarNum > 0) {
                    for (let i = 0; i < toolbarNum; i++) {
                        toolBarHeight += document.getElementsByClassName("toolbar")[i].offsetHeight;
                        console.log(i + " -> toolBarHeight:" + document.getElementsByClassName("toolbar")[i].offsetHeight);
                    }
                }
                console.log("toolBarHeight:" + toolBarHeight);

                // 可用于显示表格的高度
                // 表格可用高度 = 浏览器窗口可视高度 - 顶部menubar高度40 - 顶部标签栏高度40 - 头部toolbar高度  - 底部页码栏40 - 空余高度20
                let tableHeight = clientHeight - 40 - 40 - toolBarHeight - 40 - 20;

                /*// 表格每一行高度,默认40
                let tableCellHeight = 40;
                if (document.getElementsByClassName("el-table__row")[0]){
                    tableCellHeight = document.getElementsByClassName("el-table__row")[0].offsetHeight;
                }
                // console.log("tableCellHeight:" + tableCellHeight);

                // 处理表格高度为单元格高度整数倍
                if (tableHeight % tableCellHeight !== 0) {
                    tableHeight = tableHeight - tableHeight % tableCellHeight;
                }*/
                console.log("tableHeight:" + tableHeight);

                return tableHeight;
            },

            //获取域名
            getHost(env) {
                let host = "";
                //非生产环境 都直接返回测试地址
                if (process.env.NODE_ENV !== 'production') { // && process.env.NODE_ENV !== 'integration'
                    return "http://test-reg.seedeer.com:88/";
                }

                //生产环境 外部系统只有中国环境，始终返回mk地址
                if(env && env === "mk"){
                    return "https://mk.seedeer.com/";
                }

                // protocol//host （带端口号）
                console.log(window.location.protocol + "//" + window.location.host)
                if(window.location.protocol && window.location.host){
                    return host = window.location.protocol + "//" + window.location.host + "/";
                }else{
                    return "";
                }
            },

            //日期控件 左侧快捷选择 定义
            getDateShortcuts(){
                return [{
                    text: "今天",
                    onClick: (picker => {
                        let start = new Date();
                        let end = new Date();
                        start.setHours(0);
                        start.setMinutes(0);
                        start.setSeconds(0);
                        end.setHours(23);
                        end.setMinutes(59);
                        end.setSeconds(59);
                        picker.$emit('pick', [start, end]);
                    })
                }, {
                    text: "昨天",
                    onClick: (picker => {
                        let start = new Date();
                        let end = new Date();
                        start.setHours(0);
                        start.setMinutes(0);
                        start.setSeconds(0);
                        end.setHours(23);
                        end.setMinutes(59);
                        end.setSeconds(59);
                        start.setTime(start.getTime() - 3600 * 1000 * 24);
                        end.setTime(end.getTime() - 3600 * 1000 * 24);
                        picker.$emit('pick', [start, end]);
                    })
                }, {
                    text: "最近三天",
                    onClick: (picker => {
                        let start = new Date();
                        let end = new Date();
                        start.setHours(0);
                        start.setMinutes(0);
                        start.setSeconds(0);
                        end.setHours(23);
                        end.setMinutes(59);
                        end.setSeconds(59);
                        start.setTime(start.getTime() - 3600 * 1000 * 24 * 2);
                        picker.$emit('pick', [start, end]);
                    })
                }, {
                    text: "最近一周",
                    onClick: (picker => {
                        let start = new Date();
                        let end = new Date();
                        start.setHours(0);
                        start.setMinutes(0);
                        start.setSeconds(0);
                        end.setHours(23);
                        end.setMinutes(59);
                        end.setSeconds(59);
                        start.setTime(start.getTime() - 3600 * 1000 * 24 * 6);
                        picker.$emit('pick', [start, end]);
                    })
                }, {
                    text: "最近一个月",
                    onClick: (picker => {
                        let start = new Date(new Date().getFullYear(), new Date().getMonth() - 1, new Date().getDate());
                        let end = new Date();
                        end.setHours(23);
                        end.setMinutes(59);
                        end.setSeconds(59);
                        picker.$emit('pick', [start, end]);
                    })
                }, {
                    text: "最近三个月",
                    onClick: (picker => {
                        let start = new Date(new Date().getFullYear(), new Date().getMonth() - 3, new Date().getDate());
                        let end = new Date();
                        end.setHours(23);
                        end.setMinutes(59);
                        end.setSeconds(59);
                        picker.$emit('pick', [start, end]);
                    })
                }, {
                    text: "最近半年",
                    onClick: (picker => {
                        let start = new Date(new Date().getFullYear(), new Date().getMonth() - 6, new Date().getDate());
                        let end = new Date();
                        end.setHours(23);
                        end.setMinutes(59);
                        end.setSeconds(59);
                        picker.$emit('pick', [start, end]);
                    })
                }, {
                    text: "最近一年",
                    onClick: (picker => {
                        let start = new Date(new Date().getFullYear() - 1, new Date().getMonth(), new Date().getDate());
                        let end = new Date();
                        end.setHours(23);
                        end.setMinutes(59);
                        end.setSeconds(59);
                        picker.$emit('pick', [start, end]);
                    })
                }]
            },

            //清理session缓存
            clearStorage(){
                // console.log("clearStorage")
                // console.log(sessionStorage)

                //清理sessionStorage，主要存储参数对应关系
                for(let i=sessionStorage.length-1; i>=0; i--){
                    let keyName = sessionStorage.key(i);
                    // console.log('第'+ (i+1) +'条数据的键值为：' + keyName +'，数据为：' + sessionStorage.getItem(sessionStorage.key(i)));

                    // 删除匹配的缓存数据
                    // 免死特权(menus菜单不清理)
                    if(keyName.indexOf("sd_menus") !== -1){
                        continue;
                    }
                    sessionStorage.removeItem(keyName)
                }

                //清理localStorage，主要存储多语言对应关系
                for(let i=localStorage.length-1; i>=0; i--){
                    let keyName = localStorage.key(i);
                    // console.log('第'+ (i+1) +'条数据的键值为：' + keyName +'，数据为：' + localStorage.getItem(localStorage.key(i)));

                    // 删除匹配的缓存数据
                    // 免死特权(打印机设置不清理，版本号不清理)
                    if(keyName.indexOf("printSetting") !== -1 || keyName.indexOf("sdsupplier_nowVersion") !== -1 || keyName.indexOf("sdsupplier_oldVersion") !== -1){
                        continue;
                    }
                    localStorage.removeItem(keyName)
                }
            }

        }
    }
};

export default utils;
