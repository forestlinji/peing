package peing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import peing.mapper.RoleMapper;
import peing.mapper.UserInfoMapper;
import peing.mapper.UserMapper;
import peing.pojo.Role;
import peing.pojo.User;
import peing.pojo.UserInfo;
import peing.service.MailService;
import peing.service.UserService;
import peing.vo.AdminVo;
import peing.vo.BanListVo;
import peing.vo.SimpleUserInfo;
import peing.vo.UserAdminVo;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Qualifier("fakeMailServiceImpl")
    @Autowired
    private MailService mailService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public void register(User user) {
        userMapper.insert(user);
        //随机生成激活码
        String activeToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("active:"+user.getUserId(),activeToken);
        mailService.SendActiveMail(user.getEmail(),user.getUserId(),activeToken);
        roleMapper.insertUserRole(user.getUserId());
        log.info(user.getUserId()+"注册");
    }

    @Override
    public User selectUserByUsername(String username) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("username",username));
    }

    @Override
    public User selectUserByEmail(String email) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("email",email));
    }

    @Override
    public User selectUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public boolean active(Long userId,String activeCode) {
        ///判断token的正确性
        Object o = redisTemplate.opsForValue().get("active:" + userId);
        if(o == null){
            return false;
        }
        String ans = (String) o;
        if(!ans.equals(activeCode)){
            return false;
        }
        redisTemplate.delete("active:"+userId);
        User user = new User();
        user.setUserId(userId);
        user.setIsActive(true);
        userMapper.updateById(user);
        return true;
    }

    @Override
    public void resetPassword(Long userId,String email) {
        String changeToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("change:"+userId,changeToken);
        redisTemplate.expire("change:"+userId,1, TimeUnit.DAYS);
        log.debug(userId+"修改密码");
        mailService.SendForgetMail(email,userId,changeToken);
    }

    @Override
    public void update(User user) {
        userMapper.updateById(user);
    }

    @Override
    public UserInfo getCurrentUserInfo(Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        List<Role> roles = roleMapper.selectRoleByUsername(userId);
        /**
         * 将角色信息转为角色名
         */
        List<String> roleList = roles.stream().map(role -> role.getRoleName()).collect(Collectors.toList());
        userInfo.setRoles(roleList);
        return userInfo;
    }

    @Override
    public void updateInfo(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public UserInfo getUserInfoById(Long userId) {
        return userInfoMapper.selectById(userId);
    }

    @Override
    public Page<BanListVo> selectBanUser(int pageNum, int pageSize) {
        return userMapper.selectBanUser(new Page<BanListVo>(pageNum,pageSize));
    }

    @Override
    public List<SimpleUserInfo> getRandomUser(int num) {
        return userInfoMapper.getRandomUser(num);
    }

    @Override
    public Page<SimpleUserInfo> selectUserLikeUsername(String username,int pageNum,int pageSize) {
        return userInfoMapper.selectUserLikeUsername(new Page<SimpleUserInfo>(pageNum,pageSize),username);
    }

    @Override
    public Page<SimpleUserInfo> showAllUser(int pageNum, int pageSize) {
        return userInfoMapper.selectAll(new Page<SimpleUserInfo>(pageNum,pageSize));
    }

    @Override
    public void addAdmin(Long userId) {
        log.debug(userId+"被添加为管理员");
        userInfoMapper.addAdmin(userId);
    }

    @Override
    public void deleteAdmin(Long userId) {
        userInfoMapper.deleteAdmin(userId);

    }

    @Override
    public Page<AdminVo> selectAdmin(int pageNum, int pageSize) {
        return userInfoMapper.selectAdminList(new Page<AdminVo>(pageNum,pageSize));
    }

    @Override
    public Integer hasBan(Long banerId, Long banedId) {
        return userInfoMapper.hasBan(banerId,banedId);
    }

    @Override
    public Page<UserAdminVo> getUserAdminVoPage(int pageNum, int pageSize, String username) {
        return userInfoMapper.selectUserAdmin(new Page<>(pageNum,pageSize),username);
    }
}
