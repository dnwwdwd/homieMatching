<template>
  <user-card-list :user-list="userList" />
  <van-empty v-show="!userList || userList.length < 1" description="暂无符合要求的用户" />
</template>

<script setup>
import {useRoute} from "vue-router";
import {onMounted, ref} from "vue";
import myAxios from "../../plugins/myAxios.ts";
import qs from 'qs';
import UserCardList from "../UserCardList.vue";

const route = useRoute();
const { tags } = route.query;

const userList = ref([]);

onMounted(async() => {
  const userListData = await myAxios.get('/user/search/tags', {
    params: {
      tagNameList: tags
    },
    paramsSerializer: params => {
      return qs.stringify(params, { indices: false })
    }
  }).then(function (response) {
    console.log('/user/search/tags succeed', response);
    return response?.data;
  }).catch(function(error) {
    console.log('/user/search/tags error', error)
  })
  if(userListData) {
    userListData.forEach(user => {
      if(user.tags){
        user.tags = JSON.parse(user.tags);
      }
    })
    // 如果请求成功，就把响应结果返回给userList
    userList.value = userListData;
  }
})

</script>

<style scoped>

</style>