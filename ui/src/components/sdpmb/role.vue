<template>
    <el-select v-model="currentValue" :key-name="keyName" :label-name="labelName" :disable-name="disableName" :with-disabled="withDisabled" :disabled="disabled" :clearable="clearable" :filterable="filterable" :multiple="multiple" :placeholder="placeholderText" :remote="remotable" :filter-size="filterSize" :remote-method="remoteMethod">
        <el-option v-for="option in options" :loading="isLoading" :key="option[keyName]" :label="option[labelName]" :value="option[keyName]" :disabled="option[disableName]"></el-option>
    </el-select>
</template>
<script>
    import component from '../component';

    export default {
        name: 'role',
        props: {
            value: {
                required: false
            },
            keyName: {
                type: String,
                required: false,
                default: 'id'
            },
            labelName: {
                type: String,
                required: false,
                default: 'roleName'
            },
            disableName: {
                type: String,
                required: false,
                default: 'disabled'
            },
            withDisabled: {
                type: Boolean,
                required: false,
                default: true
            },
            disabled: {
                type: Boolean,
                required: false,
                default: false
            },
            clearable: {
                type: Boolean,
                required: false,
                default: true
            },
            filterable: {
                type: Boolean,
                required: false,
                default: true
            },
            multiple: {
                type: Boolean,
                required: false,
                default: true
            },
            remotable: {
                type: Boolean,
                required: false,
                default: false
            },
            filterSize : {
                type: Number,
                required: false,
                default: 0
            },
            placeholderText: {
                type: String,
                required: false,
                default: '请选择菜单'
            }
        },
        data () {
            return {
                isLoading: false,
                options: null,
                label: null
            }
        },
        model: {
            prop: 'value',
            event: 'change'
        },
        methods: {
            fetchOptions(input) {
                console.group("-----products.fetchOptions-------");
                console.debug("remotable: " + this.remotable);
                console.debug("input: " + input);
                console.debug("this.value: " + this.value);
                console.groupEnd();
                let url="/api/sysRole/listAll";
                component.fetchOptions(url, input, this);
            },
            remoteMethod(input) {
                console.group("-----ports.remoteMethod-------");
                console.debug("input: " + input);
                console.groupEnd();
                component.remoteMethod(input, this);
            }
        },
        mounted() {
            if(this.remotable === false || this.filterSize === 0) {
                this.fetchOptions(null);
            }
        },
        computed : {
            currentValue: {
                get() {
                    console.debug("get - product.value = " + this.value);
                    return this.value;
                },
                set(val) {
                    console.debug("product.currentValue.set = " + val);
                    this.$emit('change', val, this.options);
                }
            }
        }
    }
</script>

