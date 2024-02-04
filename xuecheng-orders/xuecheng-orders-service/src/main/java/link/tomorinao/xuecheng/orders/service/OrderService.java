package link.tomorinao.xuecheng.orders.service;

import link.tomorinao.xuecheng.orders.model.dto.AddOrderDto;
import link.tomorinao.xuecheng.orders.model.dto.PayRecordDto;
import link.tomorinao.xuecheng.orders.model.dto.PayStatusDto;
import link.tomorinao.xuecheng.orders.model.po.XcPayRecord;

public interface OrderService {

    /**
     * 创建商品订单
     * @param userId        用户id
     * @param addOrderDto   订单信息
     * @return  支付交易记录
     */
    PayRecordDto createOrder(String userId, AddOrderDto addOrderDto);

    /**
     * 根据支付记录id查支付记录
     * @param payNo 支付记录id
     * @return
     */
    XcPayRecord getPayRecordByPayNo(String payNo);

    /**
     * 请求支付宝查询支付结果
     * @param payNo 支付记录id
     * @return  支付记录信息
     */
    PayRecordDto queryPayResult(String payNo);

    void saveAlipayStatus(PayStatusDto payStatusDto);
}
