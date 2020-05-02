package peing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import peing.pojo.UserInfo;
import peing.vo.AdminVo;
import peing.vo.SimpleUserInfo;
import peing.vo.UserAdminVo;

import java.util.List;

@Mapper
@Repository
public interface UserInfoMapper extends BaseMapper<UserInfo> {
    /**
     * 随机展示部分用户信息
     * @param num
     * @return
     */
    @Select("select user_id,username,accept_question,avatar,introduction from user order by rand() limit #{num}")
    List<SimpleUserInfo> getRandomUser(int num);

    /**
     * 根据用户名或邮箱查找用户
     * @param simpleUserInfoPage
     * @param username 用户名或邮箱
     * @return
     */
    @Select("select user_id,username,accept_question,avatar,introduction from user where username like concat('%',#{username},'%')")
    Page<SimpleUserInfo> selectUserLikeUsername(Page<SimpleUserInfo> simpleUserInfoPage, String username);

    /**
     * 查询所有用户信息
     * @param simpleUserInfoPage
     * @return
     */
    @Select("select user_id,username,accept_question,avatar,introduction from user")
    Page<SimpleUserInfo> selectAll(Page<SimpleUserInfo> simpleUserInfoPage);

    /**
     * 添加管理员
     * @param userId
     */
    @Insert("insert ignore into user_role VALUE(#{userId},2)")
    void addAdmin(Long userId);

    /**
     * 删除管理员
     * @param userId
     */
    @Delete("delete from user_role where user_id = #{userId} and role_id = 2")
    void deleteAdmin(Long userId);

    /**
     * 查询管理员列表
     * @param adminVoPage
     * @return
     */
    @Select("select u.user_id,u.username from user u,user_role ur where u.user_id=ur.user_id and ur.role_id=2")
    Page<AdminVo> selectAdminList(Page<AdminVo> adminVoPage);

    /**
     * 查询是否被拉黑
     * @param banerId
     * @param banedId
     * @return
     */
    @Select("select 1 FROM ban b where b.baned_id = #{banedId} and b.baner_id = #{banerId} limit 1")
    Integer hasBan(Long banerId, Long banedId);

    /**
     * 查询所有用户id
     * @return
     */
    @Select("select user_id from user")
    List<Long> selectAllUserId();

    //-_-,我知道这坨没啥可读性，但不想为了一条sql再写一个xml了
    //数据库没做冗余，所有的count都要查一遍全表!
    /**
     * 管理员查询用户信息
     * @param userAdminVoPage
     * @param username
     * @return
     */
    @Select("SELECT\n" +
            "\tu.user_id,\n" +
            "\tu.username,\n" +
            "\tu.email,\n" +
            "\tu.is_active,\n" +
            "\tu.is_ban,\n" +
            "\tcount(\n" +
            "\tIF\n" +
            "\t( q.questioned_id = u.user_id, TRUE, NULL )) questioned_num,\n" +
            "\tcount(\n" +
            "\tIF\n" +
            "\t( q.questioner_id = u.user_id, TRUE, NULL )) questioner_num,\n" +
            "\tcount(\n" +
            "\tIF\n" +
            "\t( q.questioned_id = u.user_id AND q.reply IS NOT NULL, TRUE, NULL )) reply_num \n" +
            "FROM\n" +
            "\t`user` u,\n" +
            "\tquestion q \n" +
            "WHERE\n" +
            "\tu.username LIKE concat('%',#{username},'%') \n" +
            "\tOR u.email LIKE concat('%',#{username},'%') \n" +
            "GROUP BY\n" +
            "\tuser_id")
    Page<UserAdminVo> selectUserAdmin(Page<UserAdminVo> userAdminVoPage,String username);
}
