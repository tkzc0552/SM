<template>
    <el-select v-model="currentValue" :disabled="disabled" :clearable="clearable" :filterable="filterable" remote :remote-method="doSelectResult" :multiple="multiple" :placeholder="placeholderText" :showCode="showCode">
        <el-option v-for="option in options" :loading="isLoading" :key="option[keyName]" :label="option.name" :value="option[keyName]"></el-option>
    </el-select>
</template>
<script>
    import http from '../../axios/http';

    export default {
        name: 'productProject',
        props: {
            value: {
                required: false
            },
            disabled:{
                type: Boolean,
                required: false,
                default: false
            },
            clearable : {
                type: Boolean,
                required: false,
                default: true
            },
            filterable : {
                type: Boolean,
                required: false,
                default: true
            },
            multiple : {
                type: Boolean,
                required: false,
                default: false
            },
            keyName: {
                type: String,
                required: false,
                default: 'projectCode'
            },
            placeholderText: {
                type: String,
                required: false,
                default: '请输入项号'
            },
            showCode: {
                type: Boolean,
                required: false,
                default: false
            }
        },
        data () {
            return {
                isLoading: false,
                options: []
            }
        },
        model: {
            prop: 'value',
            event: 'change'
        },
        methods: {
            doSelectResult(val){
                if(val==''){
                    console.log("空值")
                }else{
                    console.log("doSelectResult -> " + val);
                    http.get("api/productProject/list/"+val).then(response => {//http://test-reg.seedeer.com:88/sdpmb/
                        this.isLoading = false;
                        this.options = response;
                        for (var i=0; i<this.options.length; i++) {
                            this.options[i].name = this.options[i].projectCode + " - " +this.options[i].projectName;
                        }
                    });
                }

            }
        },
        mounted() {
//            http.get("http://test-reg.seedeer.com:88/sdpmb/api/takmanagement/list").then(response => {
//                this.isLoading = false;
//                this.options = response;
//            });
        },
        computed : {
            currentValue: {
                get() {
                    console.log("get - this.value = " + this.value + ", this.keyName = " + this.keyName);
                    return this.value;
                },
                set(val) {
                    console.log("currentValue.set = " + val);
                    this.$emit('change', val, this.options);
                }
            }
        }
    }
</script>
<style scoped="">
</style>
