import axios from 'axios';
import sync_request from 'sync-request';
import {Message, MessageBox} from 'element-ui';
import {isNotEmpty} from "../filter";

// axios 配置
axios.defaults.timeout = 600000;
axios.defaults.withCredentials = true;
axios.defaults.headers.post['Content-Type'] = 'application/json;charset=UTF-8';

// http request 拦截器
axios.interceptors.request.use(
    config => {
        //const token = localStorage.getItem('token');
        //console.log("token : " + token);
        // if (token) {  // 判断是否存在token，如果存在的话，则每个http header都加上token
        //     config.headers.Authorization = `${token}`;
        // } else {
        //     config.headers.Authorization = `demo`;
        // }
        return config;
    },
    err => {
        Message({
            message: err.message,
            type: 'error',
            duration: 5 * 1000
        });
        return Promise.reject(err);
    }
);

// http response 拦截器
axios.interceptors.response.use(
    response => {
        // console.log(response)
        //缓存版本号
        if (response.headers.revision){
            localStorage.setItem("sdpmb_nowVersion", response.headers.revision)
        }
        return response;
    },
    error => {
        // console.log(error)
        if(error && error.response) {
            switch (error.response.status) {
                case 401: {
                    // 返回 401 清除token信息并跳转到登录页面
                    let host = "";
                    console.log(window.location.protocol + "//" + window.location.host)
                    if(window.location.protocol && window.location.host){
                        host = window.location.protocol + "//" + window.location.host;
                    }
                    window.location.href = host + process.env.API_LOGIN_URL;
                    break;
                }
                case 403: {
                    // 返回 404 清除token信息并跳转到登录页面
                    //store.commit(types.LOGOUT);
                    Message({
                        message: isNotEmpty(error.response['statusText']) ? error.response['statusText'] : error.response['data']['error'],
                        type: 'error',
                        duration: 5 * 1000
                    });
                    break;
                }
                case 404: {
                    // 返回 404 清除token信息并跳转到登录页面
                    //store.commit(types.LOGOUT);
                    Message({
                        message: isNotEmpty(error.response['statusText']) ? error.response['statusText'] : error.response['data']['error'],
                        type: 'error',
                        duration: 5 * 1000
                    });
                    break;
                }
                default : {
                    MessageBox({
                        title : '提示',
                        dangerouslyUseHTMLString: true,
                        message: isNotEmpty(error.response['data']) ? error.response['data'] : isNotEmpty(error.response['statusText']) ? error.response['statusText'] : error.response['error'],
                        type: 'warning',
                        duration: 5 * 1000
                    });
                }
            }
        } else {
            console.warn(error['message']);
        }
        return Promise.reject(error);   // 返回接口返回的错误信息
    }
);

export function axios_get(url, params) {
    return new Promise((resolve, reject) => {
        axios.get(url, params)
            .then(response => {
                //console.log("orinal response.data = " + JSON.stringify(response.data));
                //console.log("orinal response.data = " + response.data);
                resolve(response.data);
            }, err => {
                reject(err);
            })
            .catch((error) => {
                reject(error);
            })
    })
}

export function axios_post(url, params) {
    //console.log("params = " + JSON.stringify(params));
    return new Promise((resolve, reject) => {
        axios.post(url, params)
            .then(response => {
                resolve(response.data);
            }, err => {
                reject(err);
            })
            .catch((error) => {
                reject(error);
            });
    });
}

export function axios_put(url, params) {
    //console.log("params = " + JSON.stringify(params));
    return new Promise((resolve, reject) => {
        axios.put(url, params)
            .then(response => {
                resolve(response.data);
            }, err => {
                reject(err);
            })
            .catch((error) => {
                reject(error);
            });
    });
}

export function axios_delete(url) {
    return new Promise((resolve, reject) => {
        axios.delete(url)
            .then(response => {
                resolve(response.data);
            }, err => {
                reject(err);
            })
            .catch((error) => {
                reject(error);
            });
    });
}

export default {
    get: function(url, params) {
        return axios_get(url, params);
    },

    post: function(url, params) {
        return axios_post(url, params);
    },

    put: function(url, params) {
        return axios_put(url, params);
    },

    delete: function(url) {
        return axios_delete(url);
    },
    axios: function() {
        return axios;
    },
    postOrPut: function(url, method, params) {

        if(method.toLowerCase() === "post") {
            return axios_post(url, params);
        } else {
            return axios_put(url, params);
        }
    },
    sync_get: function (url) {
        let response = sync_request('GET', url);
        return response['body'];
    }
}
