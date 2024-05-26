package com.hjj.homieMatching.service.ws;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.hjj.homieMatching.config.HttpSessionConfig;
import com.hjj.homieMatching.model.domain.Chat;
import com.hjj.homieMatching.model.domain.Team;
import com.hjj.homieMatching.model.domain.User;
import com.hjj.homieMatching.model.request.MessageRequest;
import com.hjj.homieMatching.model.vo.ChatMessageVO;
import com.hjj.homieMatching.model.vo.WebSocketVO;
import com.hjj.homieMatching.service.ChatService;
import com.hjj.homieMatching.service.TeamService;
import com.hjj.homieMatching.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.hjj.homieMatching.constant.ChatConstant.*;
import static com.hjj.homieMatching.constant.UserConstant.ADMIN_ROLE;
import static com.hjj.homieMatching.constant.UserConstant.USER_LOGIN_STATE;

/**
 * WebSocket服务
 */
@Component
@Slf4j
@ServerEndpoint(value = "/websocket/{userId}/{teamId}", configurator = HttpSessionConfig.class)
public class WebSocket {
    /**
     * 保存队伍的连接信息
     */
    private static final Map<String, ConcurrentHashMap<String, WebSocket>> ROOMS = new HashMap<>();

    /**
     * 线程安全的无序的集合
     */
    private static final CopyOnWriteArraySet<Session> SESSIONS = new CopyOnWriteArraySet<>();

    /**
     * 会话池
     */
    private static final Map<String, Session> SESSION_POOL = new HashMap<>(0);
    /**
     * 用户服务
     */
    private static UserService userService;
    /**
     * 聊天服务
     */
    private static ChatService chatService;
    /**
     * 团队服务
     */
    private static TeamService teamService;

    /**
     * 房间在线人数
     */
    private static int onlineCount = 0;

    /**
     * 当前信息
     */
    private Session session;

    /**
     * http会话
     */
    private HttpSession httpSession;

    /**
     * 上网数
     *
     * @return int
     */
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    /**
     * 添加在线计数
     */
    public static synchronized void addOnlineCount() {
        WebSocket.onlineCount++;
    }

    /**
     * 子在线计数
     */
    public static synchronized void subOnlineCount() {
        WebSocket.onlineCount--;
    }

    /**
     * 集热地图服务
     *
     * @param userService 用户服务
     */
    @Resource
    public void setHeatMapService(UserService userService) {
        WebSocket.userService = userService;
    }

    /**
     * 集热地图服务
     *
     * @param chatService 聊天服务
     */
    @Resource
    public void setHeatMapService(ChatService chatService) {
        WebSocket.chatService = chatService;
    }

    /**
     * 集热地图服务
     *
     * @param teamService 团队服务
     */
    @Resource
    public void setHeatMapService(TeamService teamService) {
        WebSocket.teamService = teamService;
    }


    /**
     * 队伍内群发消息
     *
     * @param teamId 团队id
     * @param msg    消息
     */
    public static void broadcast(String teamId, String msg) {
        ConcurrentHashMap<String, WebSocket> map = ROOMS.get(teamId);
        // keySet获取map集合key的集合  然后在遍历key即可
        for (String key : map.keySet()) {
            try {
                WebSocket webSocket = map.get(key);
                webSocket.sendMessage(msg);
            } catch (Exception e) {
                log.error("exception message", e);
            }
        }
    }

    /**
     * 发送消息
     *
     * @param message 消息
     * @throws IOException ioexception
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 开放
     *
     * @param session 会话
     * @param userId  用户id
     * @param teamId  团队id
     * @param config  配置
     */
    @OnOpen
    public void onOpen(Session session,
                       @PathParam(value = "userId") String userId,
                       @PathParam(value = "teamId") String teamId,
                       EndpointConfig config) {
        log.info("onOpen called with userId: {}, teamId: {}", userId, teamId);
        try {
            if (StringUtils.isBlank(userId) || "undefined".equals(userId)) {
                sendError(userId, "参数有误");
                return;
            }
            log.info("Retrieving HttpSession from EndpointConfig...");
            HttpSession userHttpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
            log.info("HttpSession: {}", userHttpSession);

            if (userHttpSession == null) {
                log.error("HttpSession is null");
                return;
            }

            User user = (User) userHttpSession.getAttribute(USER_LOGIN_STATE);
            if (user != null) {
                this.session = session;
                this.httpSession = userHttpSession;
            }

            if (!"NaN".equals(teamId)) {
                if (!ROOMS.containsKey(teamId)) {
                    ConcurrentHashMap<String, WebSocket> room = new ConcurrentHashMap(0);
                    room.put(userId, this);
                    ROOMS.put(String.valueOf(teamId), room);
                    addOnlineCount();
                } else if (!((ConcurrentHashMap) ROOMS.get(teamId)).containsKey(userId)) {
                    ((ConcurrentHashMap) ROOMS.get(teamId)).put(userId, this);
                    addOnlineCount();
                }
            } else {
                SESSIONS.add(session);
                SESSION_POOL.put(userId, session);
                this.sendAllUsers();
            }
        } catch (Exception var8) {
            log.error("exception message", var8);
        }
    }

    /**
     * 关闭
     *
     * @param userId  用户id
     * @param teamId  团队id
     * @param session 会话
     */
    @OnClose
    public void onClose(@PathParam("userId") String userId,
                        @PathParam(value = "teamId") String teamId,
                        Session session) {
        try {
            if (!"NaN".equals(teamId)) {
                ROOMS.get(teamId).remove(userId);
                if (getOnlineCount() > 0) {
                    subOnlineCount();
                }
            } else {
                if (!SESSION_POOL.isEmpty()) {
                    SESSION_POOL.remove(userId);
                    SESSIONS.remove(session);
                }
                sendAllUsers();
            }
        } catch (Exception e) {
            log.error("exception message", e);
        }
    }

    /**
     * 消息
     *
     * @param message 消息
     * @param userId  用户id
     */
    @OnMessage
    public void onMessage(String message, @PathParam("userId") String userId) {
        if ("PING".equals(message)) {
            sendOneMessage(userId, "pong");
            return;
        }
        MessageRequest messageRequest = new Gson().fromJson(message, MessageRequest.class);
        Long toId = messageRequest.getToId();
        Long teamId = messageRequest.getTeamId();
        String text = messageRequest.getText();
        Integer chatType = messageRequest.getChatType();
        User fromUser = userService.getById(userId);
        Team team = teamService.getById(teamId);
        if (chatType == PRIVATE_CHAT) {
            // 私聊
            privateChat(fromUser, toId, text, chatType);
        } else if (chatType == TEAM_CHAT) {
            // 队伍内聊天
            teamChat(fromUser, text, team, chatType);
        } else {
            // 群聊
            hallChat(fromUser, text, chatType);
        }
    }

    /**
     * 队伍聊天
     *
     * @param user     用户
     * @param text     文本
     * @param team     团队
     * @param chatType 聊天类型
     */
    private void teamChat(User user, String text, Team team, Integer chatType) {
        ChatMessageVO chatMessageVo = new ChatMessageVO();
        WebSocketVO fromWebSocketVO = new WebSocketVO();
        BeanUtils.copyProperties(user, fromWebSocketVO);
        chatMessageVo.setFromUser(fromWebSocketVO);
        chatMessageVo.setText(text);
        chatMessageVo.setTeamId(team.getId());
        chatMessageVo.setChatType(chatType);
        chatMessageVo.setCreateTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        if (Objects.equals(user.getId(), team.getUserId()) || user.getUserRole() == ADMIN_ROLE) {
            chatMessageVo.setIsAdmin(true);
        }
        User loginUser = (User) this.httpSession.getAttribute(USER_LOGIN_STATE);
        if (Objects.equals(loginUser.getId(), user.getId())) {
            chatMessageVo.setIsMy(true);
        }
        String toJson = new Gson().toJson(chatMessageVo);
        try {
            broadcast(String.valueOf(team.getId()), toJson);
            saveChat(user.getId(), null, text, team.getId(), chatType);
            chatService.deleteKey(CACHE_CHAT_TEAM, String.valueOf(team.getId()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 大厅聊天
     *
     * @param user     用户
     * @param text     文本
     * @param chatType 聊天类型
     */
    private void hallChat(User user, String text, Integer chatType) {
        ChatMessageVO chatMessageVo = new ChatMessageVO();
        WebSocketVO fromWebSocketVO = new WebSocketVO();
        BeanUtils.copyProperties(user, fromWebSocketVO);
        chatMessageVo.setFromUser(fromWebSocketVO);
        chatMessageVo.setText(text);
        chatMessageVo.setChatType(chatType);
        chatMessageVo.setCreateTime(DateUtil.format(new Date(), "yyyy年MM月dd日 HH:mm:ss"));
        if (user.getUserRole() == ADMIN_ROLE) {
            chatMessageVo.setIsAdmin(true);
        }
        User loginUser = (User) this.httpSession.getAttribute(USER_LOGIN_STATE);
        if (Objects.equals(loginUser.getId(), user.getId())) {
            chatMessageVo.setIsMy(true);
        }
        String toJson = new Gson().toJson(chatMessageVo);
        sendAllMessage(toJson);
        saveChat(user.getId(), null, text, null, chatType);
        chatService.deleteKey(CACHE_CHAT_HALL, String.valueOf(user.getId()));
    }

    /**
     * 私聊
     *
     * @param user     用户
     * @param toId     为id
     * @param text     文本
     * @param chatType 聊天类型
     */
    private void privateChat(User user, Long toId, String text, Integer chatType) {
        ChatMessageVO chatMessageVo = chatService
                .chatResult(user.getId(), toId, text, chatType, DateUtil.date(System.currentTimeMillis()));
        User loginUser = (User) this.httpSession.getAttribute(USER_LOGIN_STATE);
        if (Objects.equals(loginUser.getId(), user.getId())) {
            chatMessageVo.setIsMy(true);
        }
        String toJson = new Gson().toJson(chatMessageVo);
        sendOneMessage(toId.toString(), toJson);
        saveChat(user.getId(), toId, text, null, chatType);
        chatService.deleteKey(CACHE_CHAT_PRIVATE, user.getId() + "" + toId);
        chatService.deleteKey(CACHE_CHAT_PRIVATE, toId + "" + user.getId());
    }

    /**
     * 保存聊天
     *
     * @param userId   用户id
     * @param toId     为id
     * @param text     文本
     * @param teamId   团队id
     * @param chatType 聊天类型
     */
    private void saveChat(Long userId, Long toId, String text, Long teamId, Integer chatType) {
//        if (chatType == PRIVATE_CHAT) {
//            User user = userService.getById(userId);
//            Set<Long> userIds = stringJsonListToLongSet(user.getFriendIds());
//            if (!userIds.contains(toId)) {
//                sendError(String.valueOf(userId), "该用户不是你的好友");
//                return;
//            }
//        }
        Chat chat = new Chat();
        chat.setFromId(userId);
        chat.setText(String.valueOf(text));
        chat.setChatType(chatType);
        chat.setCreateTime(new Date());
        if (toId != null && toId > 0) {
            chat.setToId(toId);
        }
        if (teamId != null && teamId > 0) {
            chat.setTeamId(teamId);
        }
        chatService.save(chat);
    }

    /**
     * 发送失败
     *
     * @param userId       用户id
     * @param errorMessage 错误消息
     */
    private void sendError(String userId, String errorMessage) {
        JSONObject obj = new JSONObject();
        obj.set("error", errorMessage);
        sendOneMessage(userId, obj.toString());
    }

    /**
     * 广播消息
     *
     * @param message 消息
     */
    public void sendAllMessage(String message) {
        for (Session userSession : SESSIONS) {
            try {
                if (userSession.isOpen()) {
                    synchronized (userSession) {
                        userSession.getBasicRemote().sendText(message);
                    }
                }
            } catch (Exception e) {
                log.error("exception message", e);
            }
        }
    }


    /**
     * 发送一个消息
     *
     * @param userId  用户编号
     * @param message 消息
     */
    public void sendOneMessage(String userId, String message) {
        Session userSession = SESSION_POOL.get(userId);
        if (userSession != null && userSession.isOpen()) {
            try {
                synchronized (userSession) {
                    userSession.getBasicRemote().sendText(message);
                }
            } catch (Exception e) {
                log.error("exception message", e);
            }
        }
    }

    /**
     * 给所有用户
     */
    public void sendAllUsers() {
        HashMap<String, List<WebSocketVO>> stringListHashMap = new HashMap<>(0);
        List<WebSocketVO> webSocketVos = new ArrayList<>();
        stringListHashMap.put("users", webSocketVos);
        for (Serializable key : SESSION_POOL.keySet()) {
            User user = userService.getById(key);
            WebSocketVO webSocketVO = new WebSocketVO();
            BeanUtils.copyProperties(user, webSocketVO);
            webSocketVos.add(webSocketVO);
        }
        sendAllMessage(JSONUtil.toJsonStr(stringListHashMap));
    }
}
