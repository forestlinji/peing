package peing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import peing.pojo.Ban;

@Mapper
@Repository
public interface BanMapper extends BaseMapper<Ban> {
}
