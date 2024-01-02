package com.hjj.homieMatching.once;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

// 有个很重要的点 TableListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
@Slf4j
public class TableListener implements ReadListener<PlanetUserInfo> {

    @Override
    public void invoke(PlanetUserInfo planetUserInfo, AnalysisContext analysisContext) {

    }
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        ReadListener.super.invokeHead(headMap, context);
    }
}