package com.hjj.homieMatching.manager;

import com.hjj.homieMatching.model.vo.SignInInfoVO;
import com.hjj.homieMatching.utils.DateUtils;
import org.redisson.api.RBitSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SignInManager {

    @Resource
    private RedissonClient redissonClient;

    public boolean isSignIn(String key) {
        int days = DateUtils.getGapDayFromFirstDayOfYear();
        RBitSet bitSet = redissonClient.getBitSet(key);
        return bitSet.get(days);
    }

    public void signIn(String key) {
        RBitSet bitSet = redissonClient.getBitSet(key);
        int days = DateUtils.getGapDayFromFirstDayOfYear();
        bitSet.set(days, true);
    }

    public SignInInfoVO getSignInInfo(String key) {
        RBitSet bitSet = redissonClient.getBitSet(key);
        SignInInfoVO signInInfoVO = new SignInInfoVO();
        signInInfoVO.setIsSignedIn(this.isSignIn(key));
        signInInfoVO.setSignedInDayNum((int) bitSet.cardinality());
        List<Integer> signedInDates = new ArrayList<>();
        List<Integer> unSignedInDates = new ArrayList<>();
        for (int i = 0; i < bitSet.length(); i++) {
            if (bitSet.get(i)) {
                signedInDates.add(i);
            } else {
                unSignedInDates.add(i);
            }
        }
        LocalDate today = LocalDate.now();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        List<java.util.Date> signedInDateList = signedInDates.stream().map(signedInDateIndex -> {
//            LocalDate signedInLocalDate = today.minusDays(DateUtils.getGapDayFromFirstDayOfYear() - signedInDateIndex);
//            return Date.from(signedInLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//        }).collect(Collectors.toList());
        List<java.util.Date> signedInDateList = new ArrayList<>();
        LocalDate date1 = LocalDate.of(2024, Month.AUGUST, 3);
        signedInDateList.add(java.sql.Date.valueOf(date1));

        // 添加日期为 2024 年 8 月 8 日
        LocalDate date2 = LocalDate.of(2024, Month.AUGUST, 8);
        signedInDateList.add(java.sql.Date.valueOf(date2));
        System.out.println(signedInDateList);
        signInInfoVO.setSignedInDates(signedInDateList);
        return signInInfoVO;
    }

}
