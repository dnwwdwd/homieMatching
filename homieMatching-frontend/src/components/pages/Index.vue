<template>
  <user-card-list :user-list="userList"/>
  <van-empty v-show="!userList || userList.length < 1" description="数据为空" />
</template>

<script setup>
import {useRoute} from "vue-router";
import {onMounted, ref} from "vue";
import myAxios from "../../plugins/myAxios.ts";
import UserCardList from "../UserCardList.vue";
const route = useRoute();
const { tags } = route.query;

const userList = ref([]);

onMounted(async() => {
  const userListData = await myAxios.get('/user/recommend', {
    params: {
      pageSize: 8,
      pageNum: 1,
    },

  }).then(function (response) {
    console.log('/user/recommend succeed', response);
    return response?.data?.records;
  }).catch(function(error) {
    console.log('/user/recommend error', error)
  })
  console.log(userListData);
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