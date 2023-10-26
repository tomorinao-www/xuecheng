package link.tomorinao.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import link.tomorinao.xuecheng.content.mapper.MqMessageMapper;
import link.tomorinao.xuecheng.content.model.po.MqMessage;
import link.tomorinao.xuecheng.content.service.IMqMessageService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tomorinao
 * @since 2023-10-04
 */
@Service
public class MqMessageServiceImpl extends ServiceImpl<MqMessageMapper, MqMessage> implements IMqMessageService {

}
