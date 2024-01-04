import myAxios from "../plugins/myAxios";
import {getCurrentUserState, setCurrentUserState} from "../states/user.ts";

export const getCurrentUser = async () => {
    const currentUser = getCurrentUserState();
    if(currentUser) {
        return currentUser;
    }
    // 不存在则从远程获取
    const res = await myAxios.get('/user/current');
    if(res.data) {
        setCurrentUserState(res.data);
        return res.data;
    }
    return await myAxios.get('/user/current');
}