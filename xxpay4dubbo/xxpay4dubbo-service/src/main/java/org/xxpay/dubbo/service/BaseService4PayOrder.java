package org.xxpay.dubbo.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.xxpay.common.constant.PayConstant;
import org.xxpay.dal.dao.mapper.PayOrderMapper;
import org.xxpay.dal.dao.model.PayOrder;
import org.xxpay.dal.dao.model.PayOrderExample;

import java.util.List;

/**
 * Description 基础支付订单操作
 */
@Service
public class BaseService4PayOrder extends BaseService {

    @Autowired
    private PayOrderMapper payOrderMapper;

    /**
     * Description 创建订单
     *
     * @param payOrder 订单
     * @return
     */
    public int baseCreatePayOrder(PayOrder payOrder) {
        return payOrderMapper.insertSelective(payOrder);
    }

    /**
     * Description 按订单号查询订单
     *
     * @param payOrderId 订单号
     * @return
     */
    public PayOrder baseSelectPayOrder(String payOrderId) {
        return payOrderMapper.selectByPrimaryKey(payOrderId);
    }

    /**
     * Description 按商户号和支付订单号查询订单
     *
     * @param mchId      商户号
     * @param payOrderId 支付订单号
     * @return
     */
    public PayOrder baseSelectByMchIdAndPayOrderId(String mchId, String payOrderId) {
        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria criteria = example.createCriteria();
        criteria.andMchIdEqualTo(mchId);
        criteria.andPayOrderIdEqualTo(payOrderId);
        List<PayOrder> payOrderList = payOrderMapper.selectByExample(example);
        return CollectionUtils.isEmpty(payOrderList) ? null : payOrderList.get(0);
    }

    /**
     * Description 按商户号和商户订单号查询订单
     *
     * @param mchId      商户号
     * @param mchOrderNo 商户订单号
     * @return
     */
    public PayOrder baseSelectByMchIdAndMchOrderNo(String mchId, String mchOrderNo) {
        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria criteria = example.createCriteria();
        criteria.andMchIdEqualTo(mchId);
        criteria.andMchOrderNoEqualTo(mchOrderNo);
        List<PayOrder> payOrderList = payOrderMapper.selectByExample(example);
        return CollectionUtils.isEmpty(payOrderList) ? null : payOrderList.get(0);
    }

    /**
     * Description 更新支付订单支付状态为：支付中
     *
     * @param payOrderId     支付订单id
     * @param channelOrderNo 渠道订单号（例如：微信支付的订单号）
     * @return
     */
    public int baseUpdateStatus4Ing(String payOrderId, String channelOrderNo) {
        PayOrder payOrder = new PayOrder();
        payOrder.setStatus(PayConstant.PAY_STATUS_PAYING);
        if (channelOrderNo != null) {
            payOrder.setChannelOrderNo(channelOrderNo);
        }
        payOrder.setPaySuccTime(System.currentTimeMillis());
        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria criteria = example.createCriteria();
        criteria.andPayOrderIdEqualTo(payOrderId);
        criteria.andStatusEqualTo(PayConstant.PAY_STATUS_INIT);
        return payOrderMapper.updateByExampleSelective(payOrder, example);
    }

    /**
     * Description 更新支付订单支付状态为：支付成功
     *
     * @param payOrderId 支付订单号
     * @return
     */
    public int baseUpdateStatus4Success(String payOrderId) {
        return baseUpdateStatus4Success(payOrderId, null);
    }

    /**
     * Description 更新支付订单支付状态为：支付成功
     * 注：支付中-> 支付成功
     *
     * @param payOrderId     支付订单号
     * @param channelOrderNo 渠道订单号
     * @return
     */
    public int baseUpdateStatus4Success(String payOrderId, String channelOrderNo) {
        PayOrder payOrder = new PayOrder();
        payOrder.setPayOrderId(payOrderId);
        payOrder.setStatus(PayConstant.PAY_STATUS_SUCCESS);
        payOrder.setPaySuccTime(System.currentTimeMillis());
        if (StringUtils.isNotBlank(channelOrderNo)) {
            payOrder.setChannelOrderNo(channelOrderNo);
        }

        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria criteria = example.createCriteria();
        criteria.andPayOrderIdEqualTo(payOrderId);
        criteria.andStatusEqualTo(PayConstant.PAY_STATUS_PAYING);
        return payOrderMapper.updateByExampleSelective(payOrder, example);
    }

    /**
     * Description 更新支付订单支付状态为：已完成
     *
     * @param payOrderId 支付订单号
     * @return
     */
    public int baseUpdateStatus4Complete(String payOrderId) {
        PayOrder payOrder = new PayOrder();
        payOrder.setPayOrderId(payOrderId);
        payOrder.setStatus(PayConstant.PAY_STATUS_COMPLETE);

        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria criteria = example.createCriteria();
        criteria.andPayOrderIdEqualTo(payOrderId);
        criteria.andStatusEqualTo(PayConstant.PAY_STATUS_SUCCESS);
        return payOrderMapper.updateByExampleSelective(payOrder, example);
    }

    /**
     * Description 设置支付订单-通知次数；
     *
     * @param payOrderId 支付订单号
     * @param count      通知次数
     * @return
     */
    public int baseUpdateNotify(String payOrderId, byte count) {
        PayOrder newPayOrder = new PayOrder();
        newPayOrder.setNotifyCount(count);
        newPayOrder.setLastNotifyTime(System.currentTimeMillis());
        newPayOrder.setPayOrderId(payOrderId);
        return payOrderMapper.updateByPrimaryKeySelective(newPayOrder);
    }

}
