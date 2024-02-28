package com.hjj.homieMatching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjj.homieMatching.common.ErrorCode;
import com.hjj.homieMatching.constant.RedisConstant;
import com.hjj.homieMatching.constant.UserConstant;
import com.hjj.homieMatching.exception.BusinessException;
import com.hjj.homieMatching.mapper.UserMapper;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.model.vo.UserVO;
import com.hjj.homieMatching.service.UserService;
import com.hjj.homieMatching.utils.AlgorithmUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.hjj.homieMatching.constant.UserConstant.ADMIN_ROLE;
import static com.hjj.homieMatching.constant.UserConstant.USER_LOGIN_STATE;

/**用户服务实现类
 *
* @author 17653
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2023-10-04 13:54:07
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService{
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserMapper userMapper;
    /**
     * 盐值为'hjj'，用以混淆密码
     */
    private static final String SALT="hjj";
    @Override
    @Transactional( rollbackFor = Exception.class)
    public long userRegister(String userAccount, String userPassword, String checkPassword ,String planetCode,
                                 Integer gender, String avatarUrl, String username, String phone,
                                 List<String> tagNameList, Double longitude, Double dimension){
        //1.校验
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)){
            // todo 修改为自定义异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if(userPassword.length() < 8||checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        //对星球编码进行校验
        if(planetCode.length() > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号过长");
        }
        //账户不能包含特殊字符
        String validPattern="[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher= Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号含有特殊字符");
        }
        //密码和校验密码得相同
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码和校验密码不相同");
        }
        //账户不能重复
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = userMapper.selectCount(queryWrapper);//将查询条件注入进去
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }
        //编号重复
        queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("planetCode",planetCode);
        count = userMapper.selectCount(queryWrapper);//将查询条件注入进去
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号已存在");
        }
        if(avatarUrl.length() >= 1024) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "头像参数过长");
        }
        if (gender == null || gender <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (phone.length() > 11) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号不合法");
        }
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请至少选择一个标签");
        }
        if (longitude == null || longitude > 180 || longitude < -180) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "坐标经度不合法");
        }
        if (dimension == null || dimension > 90 || dimension < -90) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "坐标维度不合法");
        }
        //2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes());
        //3.插入数据
        User user=new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        user.setGender(gender);
        user.setAvatarUrl(avatarUrl);
        user.setUsername(username);
        user.setPhone(phone);
        user.setLongitude(longitude);
        user.setDimension(dimension);
        // 处理用户标签
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[');
        for (int i = 0;i < tagNameList.size();i++) {
            stringBuilder.append('"').append(tagNameList.get(i)).append('"');
            if (i < tagNameList.size() - 1) {
                stringBuilder.append(',');
            }
        }
        stringBuilder.append(']');
        user.setTags(stringBuilder.toString());
        boolean saveResult = this.save(user);
        if(!saveResult){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "添加失败");
        }
        // 如果用户信息插入数据库，则计算用户坐标信息并存入Redis
        Long addToRedisResult = stringRedisTemplate.opsForGeo().add(RedisConstant.USER_GEO_KEY,
                new Point(user.getLongitude(), user.getDimension()), String.valueOf(user.getId()));
        if (addToRedisResult == null || addToRedisResult <= 0) {
            log.error("用户注册时坐标信息存入Redis失败");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.校验
        if(StringUtils.isAnyBlank(userAccount, userPassword)){
            return null;
        }
        if(userAccount.length() < 4){
            return null;
        }
        if(userPassword.length() < 8){
            return null;
        }
        // 账户不能包含特殊字符
        String validPattern="[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher= Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            return null;
        }
        // 2.加密
        String encryptPassword=DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if(user==null){
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }
        // 3.用户脱敏
        User safetyUser=getSafetyUser(user);
        // 4.记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }
    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser){
        if (originUser == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User safetyUser=new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setTags(originUser.getTags());
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * tagList 用户要拥有的标签
     * @param tagNameList
     * @return
     */
    @Override
    public List<UserVO> searchUsersByTags(List<String> tagNameList, HttpServletRequest request) {
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = getLoginUser(request);
        // 定义开始时间
        long startTime = System.currentTimeMillis();
        // 1.先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("id", loginUser.getId());
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        // 2.在内存中判断是否包含要求的标签
        List<User> userListResult = userList.stream().filter(user -> {
            String tagStr = user.getTags();
            if (StringUtils.isBlank(tagStr)) {
                return false;
            }
            //将数据库中的json字符串反序列化为java对象
            Set<String> tagNameSet = gson.fromJson(tagStr, new TypeToken<Set<String>>() {
            }.getType());
            tagNameSet = Optional.ofNullable(tagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
        String redisUserGeoKey = RedisConstant.USER_GEO_KEY;
        return userListResult.stream().map(user -> {
            Distance distance = stringRedisTemplate.opsForGeo().distance(redisUserGeoKey, String.valueOf(loginUser.getId()),
                    String.valueOf(user.getId()), RedisGeoCommands.DistanceUnit.KILOMETERS);
            UserVO userVO = new UserVO();
            userVO.setId(user.getId());
            userVO.setUsername(user.getUsername());
            userVO.setUserAccount(user.getUserAccount());
            userVO.setAvatarUrl(user.getAvatarUrl());
            userVO.setGender(user.getGender());
            userVO.setProfile(user.getProfile());
            userVO.setPhone(user.getPhone());
            userVO.setEmail(user.getEmail());
            userVO.setUserStatus(user.getUserStatus());
            userVO.setCreateTime(user.getCreateTime());
            userVO.setUpdateTime(user.getUpdateTime());
            userVO.setUserRole(user.getUserRole());
            userVO.setPlanetCode(user.getPlanetCode());
            userVO.setTags(user.getTags());
            userVO.setDistance(distance.getValue());
            return userVO;
        }).collect(Collectors.toList());
    }

    @Override
    public int updateUser(@RequestBody User user, User loginUser) {
        long userId = user.getId();
        // 如果是管理员允许更新任意用户信息
        if(userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 补充更多校验，如果用户传的值只有id，没有其它参数则不执行更新操作
        // 如果是管理员，允许更新任意用户信息
        // 如果不是管理员，只允许更新当前用户信息
        if(!isAdmin(loginUser) && userId != loginUser.getId()){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        return userMapper.updateById(user);
    }
    @Override
    public User getLoginUser(HttpServletRequest request){
        if(request == null) {
            return null;
        }
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if(userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return (User) userObj;
    }

    // 使用SQL实现标签的查询，@Deprecated表示该方法是过时的，暂不使用。private修饰该方法是为防止被误调用
    @Deprecated
    private List<User> searchUsersByTagsBySQL(List<String> tagNameList){
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        for(String tagName : tagNameList){
            queryWrapper = queryWrapper.like("tags", tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user=(User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    //
    @Override
    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getUserRole() == ADMIN_ROLE;
    }

    @Override
    public List<UserVO> matchUsers(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("tags");
        queryWrapper.ne("id", loginUser.getId());
        queryWrapper.select("id","tags");
        List<User> userList = this.list(queryWrapper);

        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下表 => 相似度'
        List<Pair<User,Long>> list = new ArrayList<>();
        // 依次计算当前用户和所有用户的相似度
        for (int i = 0; i <userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            //无标签的 或当前用户为自己
            if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()){
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            //计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user,distance));
        }
        //按编辑距离有小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        //有顺序的userID列表
        List<Long> userListVo = topUserPairList.stream().map(pari -> pari.getKey().getId()).collect(Collectors.toList());

        //根据id查询user完整信息
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id",userListVo);
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper).stream()
                .map(user -> getSafetyUser(user))
                .collect(Collectors.groupingBy(User::getId));

        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userListVo){
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }

        String redisUserGeoKey = RedisConstant.USER_GEO_KEY;
        List<UserVO> finalUserVOList = finalUserList.stream().map(user -> {
            Distance distance = stringRedisTemplate.opsForGeo().distance(redisUserGeoKey, String.valueOf(loginUser.getId()),
                    String.valueOf(user.getId()), RedisGeoCommands.DistanceUnit.KILOMETERS);

            UserVO userVO = new UserVO();
            userVO.setId(user.getId());
            userVO.setUsername(user.getUsername());
            userVO.setUserAccount(user.getUserAccount());
            userVO.setAvatarUrl(user.getAvatarUrl());
            userVO.setGender(user.getGender());
            userVO.setProfile(user.getProfile());
            userVO.setPhone(user.getPhone());
            userVO.setEmail(user.getEmail());
            userVO.setUserStatus(user.getUserStatus());
            userVO.setCreateTime(user.getCreateTime());
            userVO.setUpdateTime(user.getUpdateTime());
            userVO.setUserRole(user.getUserRole());
            userVO.setPlanetCode(user.getPlanetCode());
            userVO.setTags(user.getTags());
            userVO.setDistance(distance.getValue());
            return userVO;

        }).collect(Collectors.toList());
        return finalUserVOList;
    }

    @Override
    public List<UserVO> searchNearby(int radius, User loginUser) {
        String geoKey = RedisConstant.USER_GEO_KEY;
        String userId = String.valueOf(loginUser.getId());
        Double longitude = loginUser.getLongitude();
        Double dimension = loginUser.getDimension();
        if (longitude == null || dimension == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "登录用户经纬度参数为空");
        }
        Distance geoRadius = new Distance(radius, RedisGeoCommands.DistanceUnit.KILOMETERS);
        Circle circle = new Circle(new Point(longitude, dimension), geoRadius);
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo()
                .radius(geoKey, circle);
        List<Long> userIdList = new ArrayList<>();
        for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : results) {
            String id = result.getContent().getName();
            if (!userId.equals(id)) {
                userIdList.add(Long.parseLong(id));
            }
        }
        List<UserVO> userVOList = userIdList.stream().map(
                id -> {
                    UserVO userVO = new UserVO();
                    User user = this.getById(id);
                    BeanUtils.copyProperties(user, userVO);
                    Distance distance = stringRedisTemplate.opsForGeo().distance(geoKey, userId, String.valueOf(id),
                            RedisGeoCommands.DistanceUnit.KILOMETERS);
                    userVO.setDistance(distance.getValue());
                    return userVO;
                }
        ).collect(Collectors.toList());
        return userVOList;
    }
}
