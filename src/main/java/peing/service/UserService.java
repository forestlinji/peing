package peing.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import peing.pojo.User;
import peing.pojo.UserInfo;
import peing.vo.AdminVo;
import peing.vo.BanListVo;
import peing.vo.SimpleUserInfo;
import peing.vo.UserAdminVo;

import java.util.List;


@Service
public interface UserService {
    /**
     * 用户注册
     * @param user
     */
    void register(User user);

    /**
     * 根据用户名或邮箱查找用户
     * @param username
     * @return
     */
    User selectUserByUsername(String username);

    /**
     * 根据邮箱查找用户
     * @param email
     * @return
     */
    User selectUserByEmail(String email);

    /**
     * 根据id查找用户
     * @param userId
     * @return
     */
    User selectUserById(Long userId);

    /**
     * 用户激活
     * @param userId
     * @param activeCode
     * @return
     */
    boolean active(Long userId,String activeCode);

    /**
     * 重置密码
     * @param userId
     * @param email
     */
    void resetPassword(Long userId,String email);

    /**
     * 更新用户信息
     * @param user
     */
    void update(User user);

    /**
     * 获取用户信息(含权限)
     * @param userId
     * @return
     */
    UserInfo getCurrentUserInfo(Long userId);

    /**
     * 更新用户普通信息(提问箱等)
     * @param userInfo
     */
    void updateInfo(UserInfo userInfo);

    /**
     * 根据id查找用户信息
     * @param userId
     * @return
     */
    UserInfo getUserInfoById(Long userId);

    /**
     * 查找被封禁的用户
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<BanListVo> selectBanUser(int pageNum, int pageSize);

    /**
     * 随机展示用户
     * @param num
     * @return
     */
    List<SimpleUserInfo> getRandomUser(int num);

    /**
     * 根据用户名或邮箱查找用户信息
     * @param username
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<SimpleUserInfo> selectUserLikeUsername(String username,int pageNum,int pageSize);

    /**
     * 查询所有用户
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<SimpleUserInfo> showAllUser(int pageNum, int pageSize);

    /**
     * 添加管理员
     * @param userId
     */
    void addAdmin(Long userId);

    /**
     * 删除管理员
     * @param userId
     */
    void deleteAdmin(Long userId);

    /**
     * 查询管理员
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<AdminVo> selectAdmin(int pageNum, int pageSize);

    /**
     * 查询是否被拉黑
     * @param banerId
     * @param banedId
     * @return
     */
    Integer hasBan(Long banerId, Long banedId);

    /**
     * 管理员查询用户信息
     * @param pageNum
     * @param pageSize
     * @param username
     * @return
     */
    Page<UserAdminVo> getUserAdminVoPage(int pageNum, int pageSize, String username);
}
