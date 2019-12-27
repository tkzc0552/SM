import {isEmpty, isNotEmpty, isObjectEmpty} from "../filter";
import http from "../axios/http";


export default {
    fetchOptions: function (url, input, self, callback) {
        let paramsUrl = null;
        if (!self.withDisabled) {
            paramsUrl = "?search_eq_delFlag=false";
        }
        let params = {};
        if(self && typeof self.buildParams === "function") {
            params = self.buildParams();
        }
        if (!isObjectEmpty(params)) {
            for (let paramName in params) {
                let paramValue = params[paramName];
                if (isNotEmpty(paramValue)) {
                    if (paramsUrl) {
                        paramsUrl += "&";
                    } else {
                        paramsUrl = "?";
                    }
                    paramsUrl += "search_eq_" + paramName + "=" + paramValue;
                }
            }
        }
        if(self.filterSize > 0) {
            let hasInput = false;
            if (isNotEmpty(input) && input.length >= self.filterSize && (self.label == null || self.label !== input) && input !== self.value) {
                if (paramsUrl) {
                    paramsUrl += "&";
                } else {
                    paramsUrl = "?";
                }
                paramsUrl += "search_like_" + self.labelName + "=" + input;
                hasInput = true;
            }
            if(hasInput === false) {
                if(isNotEmpty(self.value)) {
                    if(paramsUrl) {
                        paramsUrl += "&";
                    } else {
                        paramsUrl = "?";
                    }
                    paramsUrl += "search_eq_" + self.keyName + "=" + self.value;
                } else {
                    self.options = null;
                    return;
                }
            }
        }

        if (paramsUrl) {
            url += paramsUrl;
        }
        console.debug("url : " + url);
        let isProduction = process.env.NODE_ENV === 'production';
        console.debug("isProduction : " + isProduction);
        let cache = null;
        if (isProduction) {
            cache = window.sessionStorage.getItem(url);
            console.debug("cache : " + cache);
            if (isNotEmpty(cache)) {
                self.options = JSON.parse(cache);
                if (self.label !== undefined) {
                    this.setLabel(self);
                }
                if(callback){
                    callback();
                }
            }
        }

        if (cache == null || self.options === null || self.options === undefined) {
            http.get(url).then(response => {
                self.isLoading = false;
                self.options = response;
                if (self.label !== undefined) {
                    this.setLabel(self);
                }
                if(callback){
                    callback();
                }
                if (isProduction) {
                    window.sessionStorage.setItem(url, JSON.stringify(response));
                }
            });
        }
    },
    setLabel(self) {
        if (self.options != null) {
            for (let i = 0; i < self.options.length; i++) {
                if (self.options[i][self.keyName] === self.value) {
                    self.label = self.options[i][self.labelName];
                    break;
                }
            }
        }
    },
    remoteMethod(input, self) {
        console.debug("input: " + input);
        if (self.remotable === false) {
            return;
        }
        if (isEmpty(input)) {
            return;
        }
        if (self.filterSize === 0 || input.length >= self.filterSize) {
            console.debug("input.length >= " + self.filterSize);
            self.isLoading = true;
            self.fetchOptions(input);
        }
    }
}
