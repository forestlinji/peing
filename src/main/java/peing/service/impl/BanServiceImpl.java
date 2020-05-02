package peing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import peing.mapper.BanMapper;
import peing.pojo.Ban;
import peing.service.BanService;


@Service
public class BanServiceImpl extends ServiceImpl<BanMapper, Ban> implements BanService {
    @Autowired
    private BanMapper banMapper;
}
