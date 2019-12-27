<template>
    <el-radio-group v-model="currentValue" :type="typeCode" :has-all="hasAll" :all-disabled="allBtnDisabled" :disabled="disabled" :item-disabled-list="itemDisabledList">
        <el-radio-button v-if="hasAll"  :disabled="allBtnDisabled" label="">全部</el-radio-button>
        <el-radio-button v-for="(option, index) in options" :key="option[keyName]" :label="option[keyName]" :disabled="itemDisabledList[index]">{{option[labelName]}}</el-radio-button>
    </el-radio-group>
</template>
<script>
    import component from '../component';

    export default {
        name: 'sdParamRadioGroup',
        props: {
            value: {
                required: false,
                default: ''
            },
            typeCode: {
                type: String,
                required: true,
            },

            hasAll: {
                type: Boolean,
                required: false,
                default: false
            },
            allBtnDisabled: {
                type: Boolean,
                required: false,
                default: false
            },

            keyName: {
                type: String,
                required: false,
                default: 'code'
            },
            // labelName: {
            //     type: String,
            //     required: false,
            //     default: 'cname'
            // },
            disabled: {
                type: Boolean,
                required: false,
                default: false
            },
            itemDisabledList: {
                type: Array,
                required: false,
                default: function () {
                    return []
                }
            },
            withDisabled: {
                type: Boolean,
                required: false,
                default: true
            },
        },
        data() {
            return {
                labelName:  'cname',
                options: null
            }
        },
        model: {
            prop: 'value',
            event: 'change'
        },
        methods: {
            fetchOptions(input) {
                console.group("-----locations.fetchOptions-------");
                console.debug("remotable: " + this.remotable);
                console.debug("input: " + input);
                console.debug("this.value: " + this.value);
                console.groupEnd();

                let url = "api/params/queryParamDetailDetails?code=" + this.typeCode;
                component.fetchOptions(url, input, this);
            },
            remoteMethod(input) {
                console.group("-----locations.remoteMethod-------");
                console.debug("input: " + input);
                console.groupEnd();
                component.remoteMethod(input, this);
            }
        },
        mounted() {
            this.fetchOptions(null);
        },
        computed: {
            currentValue: {
                get() {
                    console.debug("get - this.value = " + this.value + ", this.keyName = " + this.keyName + ", this.labelName = " + this.labelName);
                    return this.value;
                },
                set(val) {
                    this.$emit('change', val, this.options);
                }
            },
        },
        watch: {

        }
    }
</script>
<style scoped="">
</style>
