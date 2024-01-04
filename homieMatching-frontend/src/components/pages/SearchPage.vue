<template>
  <form action="/">
    <van-search
        v-model="searchText"
        show-action
        placeholder="请输入搜索关键词"
        @search="onSearch"
        @cancel="onCancel"
    />
  </form>
  <van-divider content-position="left">已选标签</van-divider>
  <div v-if="activeIds.length === 0">请选择标签</div>
  <van-row gutter="16" style="padding: 0 16px">
    <van-col>
      <van-tag v-for="tag in activeIds"  closeable size="medium" type="primary" @close="doClose(tag)">
        {{tag}}
      </van-tag>
    </van-col>
  </van-row>
  <van-divider content-position="left">选择标签</van-divider>
  <van-tree-select
      v-model:active-id="activeIds"
      v-model:main-active-index="activeIndex"
      :items="tagList"
  />
  <div style="padding: 12px">
    <van-button block type="primary" @click="doSearchResult" style="margin: 12px">搜索</van-button>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import {useRouter} from 'vue-router';
const router = useRouter();

const searchText = ref('');
let originTagList = [
  {
    text: '性别',
    children: [
      { text: '男', id: '男' },
      { text: '女', id: '女' },
    ],
  },
  {
    text: '年级',
    children: [
      { text: '大一', id: '大一' },
      { text: '大二', id: '大二' },
      { text: '大三', id: '大三' },
    ],
  },
  /*{ text: '福建', disabled: true },*/
];
let tagList = ref(originTagList);
const onSearch = () => {
  tagList.value = originTagList.map(parentTag => {
    if (Array.isArray(parentTag.children) && parentTag.children.length > 0) {
      const tempChildren = parentTag.children.filter(item => item.text.includes(searchText.value));
      const tempParentTag = { ...parentTag };
      tempParentTag.children = tempChildren;
      return tempParentTag;
    }
    return parentTag;
  });
};

const onCancel = () => {
  searchText.value = '';
  tagList.value = originTagList;
}

// 已选中的标签
const activeIds = ref([]);
const activeIndex = ref(0);

// 一处标签
function doClose(tag) {
  activeIds.value = activeIds.value.filter(item => {
    return item !== tag
  });
}

const doSearchResult = () =>{
  // 执行搜索后跳转
  router.push({
    path: '/user/list',
    query: {
      tags: activeIds.value
    }
  })
}
</script>

<style scoped>

</style>