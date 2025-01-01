package com.hjj.homieMatching.manager;

import com.hjj.homieMatching.model.vo.SignInInfoVO;
import com.hjj.homieMatching.utils.DateUtils;
import org.redisson.api.RBitSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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

    // 判断是否签到
    public boolean isSignIn(String key) {
        int days = DateUtils.getGapDayFromFirstDayOfYear();
        RBitSet bitSet = redissonClient.getBitSet(key);
        return bitSet.get(days);
    }

    // 签到
    public void signIn(String key) {
        RBitSet bitSet = redissonClient.getBitSet(key);
        int days = DateUtils.getGapDayFromFirstDayOfYear();
        bitSet.set(days, true);
    }

    // 获取签到信息
    public SignInInfoVO getSignInInfo(String key) {
        RBitSet bitSet = redissonClient.getBitSet(key);
        SignInInfoVO signInInfoVO = new SignInInfoVO();
        signInInfoVO.setIsSignedIn(this.isSignIn(key));
        signInInfoVO.setSignedInDayNum((int) bitSet.cardinality());
        List<Integer> signedInDateIndexList = new ArrayList<>();
        List<java.util.Date> signedInDateList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        if (redissonClient.getKeys().countExists(key) > 0 && bitSet.length() > 0) {
            for (int i = 0; i < bitSet.length(); i++) {
                if (bitSet.get(i)) {
                    signedInDateIndexList.add(i);
                }
            }
            signedInDateList = signedInDateIndexList.stream().map(signedInDateIndex -> {
                LocalDate signedInLocalDate = today.minusDays(DateUtils.getGapDayFromFirstDayOfYear() - signedInDateIndex);
                return Date.from(signedInLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            }).collect(Collectors.toList());
        }
        signInInfoVO.setSignedInDates(signedInDateList);
        return signInInfoVO;
    }

}
