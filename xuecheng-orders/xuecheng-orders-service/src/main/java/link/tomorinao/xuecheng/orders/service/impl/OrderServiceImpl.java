package link.tomorinao.xuecheng.orders.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import link.tomorinao.xuecheng.base.exception.XueChengException;
import link.tomorinao.xuecheng.base.utils.BeanUtils;
import link.tomorinao.xuecheng.base.utils.IdWorkerUtils;
import link.tomorinao.xuecheng.base.utils.QRCodeUtil;
import link.tomorinao.xuecheng.orders.config.AlipayConfig;
import link.tomorinao.xuecheng.orders.mapper.XcOrdersGoodsMapper;
import link.tomorinao.xuecheng.orders.mapper.XcOrdersMapper;
import link.tomorinao.xuecheng.orders.mapper.XcPayRecordMapper;
import link.tomorinao.xuecheng.orders.model.dto.AddOrderDto;
import link.tomorinao.xuecheng.orders.model.dto.PayRecordDto;
import link.tomorinao.xuecheng.orders.model.dto.PayStatusDto;
import link.tomorinao.xuecheng.orders.model.po.XcOrders;
import link.tomorinao.xuecheng.orders.model.po.XcOrdersGoods;
import link.tomorinao.xuecheng.orders.model.po.XcPayRecord;
import link.tomorinao.xuecheng.orders.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Value("${pay.alipay.APP_ID}")
    String APP_ID;

    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    String APP_PRIVATE_KEY;

    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    String ALIPAY_PUBLIC_KEY;

    @Autowired
    XcOrdersMapper xcOrdersMapper;

    @Autowired
    XcPayRecordMapper xcPayRecordMapper;

    @Autowired
    XcOrdersGoodsMapper xcOrdersGoodsMapper;

    @Autowired
    OrderServiceImpl thisProxy;

    @Value("${pay.qrcodeurl}")
    String qrcodeurl;

    @Override
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) {
        // 1. 添加商品订单
        XcOrders xcOrders = thisProxy.saveOrders(userId, addOrderDto);
        // 2. 添加支付交易记录
        XcPayRecord payRecord = createPayRecord(xcOrders);
        // 3. 生成二维码
        String qrCode = null;
        try {
            // 3.1 用订单号填充占位符
            qrcodeurl = String.format(qrcodeurl, payRecord.getPayNo());
            // 3.2 生成二维码
            qrCode = QRCodeUtil.createQRCode(qrcodeurl, 200, 200);
        } catch (IOException e) {
            XueChengException.cast("生成二维码出错");
        }
        PayRecordDto payRecordDto = new PayRecordDto();
        BeanUtils.copyProperties(payRecord, payRecordDto);
        payRecordDto.setQrcode(qrCode);
        return payRecordDto;
    }

    @Override
    public XcPayRecord getPayRecordByPayNo(String payNo) {
        return xcPayRecordMapper.selectOne(new LambdaQueryWrapper<XcPayRecord>()
                .eq(XcPayRecord::getPayNo, payNo));
    }
    /**
     * 保存订单信息，保存订单表和订单明细表，需要做幂等性判断
     *
     * @param userId      用户id
     * @param addOrderDto 选课信息
     * @return
     */
    @Transactional
    public XcOrders saveOrders(String userId, AddOrderDto addOrderDto) {
        // 1. 幂等性判断
        XcOrders order = getOrderByBusinessId(addOrderDto.getOutBusinessId());
        if (order != null) {
            return order;
        }
        // 2. 插入订单表
        order = new XcOrders();
        BeanUtils.copyProperties(addOrderDto, order);
        order.setId(IdWorkerUtils.getInstance().nextId());
        order.setCreateDate(LocalDateTime.now());
        order.setUserId(userId);
        order.setStatus("600001");
        int insert = xcOrdersMapper.insert(order);
        if (insert <= 0) {
            XueChengException.cast("插入订单记录失败");
        }
        // 3. 插入订单明细表
        Long orderId = order.getId();
        String orderDetail = addOrderDto.getOrderDetail();
        List<XcOrdersGoods> xcOrdersGoodsList = JSON.parseArray(orderDetail, XcOrdersGoods.class);
        xcOrdersGoodsList.forEach(goods -> {
            goods.setOrderId(orderId);
            int insert1 = xcOrdersGoodsMapper.insert(goods);
            if (insert1 <= 0) {
                XueChengException.cast("插入订单明细失败");
            }
        });
        return order;
    }

    private XcOrders getOrderByBusinessId(String outBusinessId) {
        LambdaQueryWrapper<XcOrders> wrapper = new LambdaQueryWrapper<XcOrders>().eq(XcOrders::getOutBusinessId, outBusinessId);
        return xcOrdersMapper.selectOne(wrapper);
    }
    public XcPayRecord createPayRecord(XcOrders orders) {
        if (orders == null) {
            XueChengException.cast("订单不存在");
        }
        if ("600002".equals(orders.getStatus())) {
            XueChengException.cast("订单已支付");
        }
        XcPayRecord payRecord = new XcPayRecord();
        payRecord.setPayNo(IdWorkerUtils.getInstance().nextId());
        payRecord.setOrderId(orders.getId());
        payRecord.setOrderName(orders.getOrderName());
        payRecord.setTotalPrice(orders.getTotalPrice());
        payRecord.setCurrency("CNY");
        payRecord.setCreateDate(LocalDateTime.now());
        payRecord.setStatus("601001");  // 未支付
        payRecord.setUserId(orders.getUserId());
        int insert = xcPayRecordMapper.insert(payRecord);
        if (insert <= 0) {
            XueChengException.cast("插入支付交易记录失败");
        }
        return payRecord;
    }

    @Override
    public PayRecordDto queryPayResult(String payNo) {

        // 1. 调用支付宝接口查询支付结果
        PayStatusDto payStatusDto = queryPayResultFromAlipay(payNo);

        // 2. 拿到支付结果，更新支付记录表和订单表的状态为 已支付
        saveAlipayStatus(payStatusDto);

        return null;
    }

    /**
     * 调用支付宝接口查询支付结果
     *
     * @param payNo 支付记录id
     * @return 支付记录信息
     */
    public PayStatusDto queryPayResultFromAlipay(String payNo) {
        // 1. 获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL, APP_ID, APP_PRIVATE_KEY, "json", AlipayConfig.CHARSET, ALIPAY_PUBLIC_KEY, AlipayConfig.SIGNTYPE);
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", payNo);
        AlipayTradeQueryResponse response = null;
        // 2. 请求查询
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            XueChengException.cast("请求支付宝查询支付结果异常");
        }
        // 3. 查询失败
        if (!response.isSuccess()) {
            XueChengException.cast("请求支付宝查询支付结果异常");
        }
        // 4. 查询成功，获取结果集
        String resultJson = response.getBody();
        // 4.1 转map
        Map resultMap = JSON.parseObject(resultJson, Map.class);
        // 4.2 获取我们需要的信息
        Map<String, String> alipay_trade_query_response = (Map) resultMap.get("alipay_trade_query_response");
        // 5. 创建返回对象
        PayStatusDto payStatusDto = new PayStatusDto();
        // 6. 封装返回
        String tradeStatus = alipay_trade_query_response.get("trade_status");
        String outTradeNo = alipay_trade_query_response.get("out_trade_no");
        String tradeNo = alipay_trade_query_response.get("trade_no");
        String totalAmount = alipay_trade_query_response.get("total_amount");
        payStatusDto.setTrade_status(tradeStatus);
        payStatusDto.setOut_trade_no(outTradeNo);
        payStatusDto.setTrade_no(tradeNo);
        payStatusDto.setTotal_amount(totalAmount);
        payStatusDto.setApp_id(APP_ID);
        return payStatusDto;
    }

    /**
     * 保存支付结果信息
     *
     * @param payStatusDto 支付结果信息
     */
    @Override
    public void saveAlipayStatus(PayStatusDto payStatusDto) {
        // 1. 获取支付流水号
        String payNo = payStatusDto.getOut_trade_no();
        // 2. 查询数据库订单状态
        XcPayRecord payRecord = getPayRecordByPayNo(payNo);
        if (payRecord == null) {
            XueChengException.cast("未找到支付记录");
        }
        XcOrders order = xcOrdersMapper.selectById(payRecord.getOrderId());
        if (order == null) {
            XueChengException.cast("找不到相关联的订单");
        }
        String statusFromDB = payRecord.getStatus();
        // 2.1 已支付，直接返回
        if ("600002".equals(statusFromDB)) {
            return;
        }
        // 3. 查询支付宝交易状态
        String tradeStatus = payStatusDto.getTrade_status();
        // 3.1 支付宝交易已成功，保存订单表和交易记录表，更新交易状态
        if ("TRADE_SUCCESS".equals(tradeStatus)) {
            // 更新支付交易表
            payRecord.setStatus("601002");
            payRecord.setOutPayNo(payStatusDto.getTrade_no());
            payRecord.setOutPayChannel("Alipay");
            payRecord.setPaySuccessTime(LocalDateTime.now());
            int updateRecord = xcPayRecordMapper.updateById(payRecord);
            if (updateRecord <= 0) {
                XueChengException.cast("更新支付交易表失败");
            }
            // 更新订单表
            order.setStatus("600002");
            int updateOrder = xcOrdersMapper.updateById(order);
            if (updateOrder <= 0) {
                log.debug("更新订单表失败");
                XueChengException.cast("更新订单表失败");
            }
        }
    }
}
