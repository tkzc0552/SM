import Vue from 'vue';
// 引入自定义组件并注册到全局
import uploadExcel from '../components/UploadExcel/index.vue';
import uploadFiles from "../components/sdpmb/uploadFiles.vue";
import uploadImg from "../components/sdpmb/uploadImg.vue";
import sdParamRadioGroup from "../components/sdpmb/sdParamRadioGroup.vue";
import sdParamGroupForTest from "../components/sdpmb/sdParamGroupForTest.vue";
import productCategory from "../components/sdpmb/productCategory.vue";
import productProject from "../components/sdpmb/productProject.vue";
import department from "../components/sdpmb/department.vue";
import departmentForAll from "../components/sdpmb/departmentForAll.vue";
import role from "../components/sdpmb/role.vue";

Vue.component('uploadExcel', uploadExcel);
Vue.component('uploadFiles', uploadFiles);
Vue.component('uploadImg', uploadImg);
Vue.component('sdParamRadioGroup', sdParamRadioGroup);
Vue.component('sdParamGroupForTest', sdParamGroupForTest);
Vue.component('productCategory', productCategory);
Vue.component('productProject', productProject);
Vue.component('department', department);
Vue.component('departmentForAll', departmentForAll);
Vue.component('role', role);

export default {}
