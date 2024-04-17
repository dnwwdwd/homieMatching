package com.hjj.homieMatching.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class PlanetUserInfo {
    @ExcelProperty("ID")
    private String ID;
    @ExcelProperty("alarm")
    private String alarm;
}
