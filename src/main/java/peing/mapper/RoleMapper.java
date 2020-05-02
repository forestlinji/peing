package peing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import peing.pojo.Role;

import java.util.List;

@Mapper
@Repository
public interface RoleMapper extends BaseMapper<Role> {
    @Select("SELECT r.role_id,r.role_name,r.description FROM role r ,user_role ur where ur.role_id = r.role_id and ur.user_id=#{userId}")
    public List<Role> selectRoleByUsername(Long userId);
    @Insert("insert into user_role (user_id,role_id) values (#{userId},1)")
    public void insertUserRole(Long userId);
}
