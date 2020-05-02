package peing.service.impl;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import peing.mapper.RoleMapper;
import peing.mapper.UserMapper;
import peing.pojo.JwtUser;
import peing.pojo.User;
import peing.service.UserService;


/**
 * @author shuang.kou
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        /**
         * 用户名或id相等都行
         */
        wrapper.eq("username",name).or().eq("email",name);
        User user = userMapper.selectOne(wrapper);
        if(user == null){
            return null;
        }
        user.setRoles(roleMapper.selectRoleByUsername(user.getUserId()));
        return new JwtUser(user);
    }

    public UserDetails loadUserByUserId(Long userId){
        User user = userMapper.selectById(userId);
        if(user == null){
            return null;
        }
        user.setRoles(roleMapper.selectRoleByUsername(user.getUserId()));
        return new JwtUser(user);
    }

}
