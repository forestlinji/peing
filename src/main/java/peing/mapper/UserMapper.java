package peing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import peing.pojo.User;
import peing.vo.BanListVo;
import peing.vo.SimpleUserInfo;

@Mapper
@Repository
public interface UserMapper extends BaseMapper<User> {
    /**
     * 查询被封禁用户
     * @param BanListVoPage
     * @return
     */
    @Select("select user_id,ban_date from user where is_ban = 1 order by ban_date desc")
    Page<BanListVo> selectBanUser(Page<BanListVo> BanListVoPage);


}
