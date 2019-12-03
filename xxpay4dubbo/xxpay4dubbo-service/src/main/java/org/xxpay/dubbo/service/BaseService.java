package org.xxpay.dubbo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.xxpay.common.constant.PayConstant;
import org.xxpay.dal.dao.mapper.MchInfoMapper;
import org.xxpay.dal.dao.mapper.MchNotifyMapper;
import org.xxpay.dal.dao.mapper.PayChannelMapper;
import org.xxpay.dal.dao.model.*;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Description 基础服务
 *
 * @author wenyi cao
 * @version 1.0.0
 * @date 2019-12-3
 */
@Service
public class BaseService {

    /**
     * Description 商户操作
     */
    @Autowired
    private MchInfoMapper mchInfoMapper;

    /**
     * Description 支付渠道操作
     */
    @Autowired
    private PayChannelMapper payChannelMapper;

    /**
     * Description 商户通知操作
     */
    @Autowired
    private MchNotifyMapper mchNotifyMapper;

    /**
     * Description 选择商户
     *
     * @param mchId 商户id
     * @return
     */
    public MchInfo baseSelectMchInfo(String mchId) {
        return mchInfoMapper.selectByPrimaryKey(mchId);
    }

    /**
     * Description 选择支付渠道
     *
     * @param mchId     商户id
     * @param channelId 渠道id
     * @return
     */
    public PayChannel baseSelectPayChannel(String mchId, String channelId) {
        PayChannelExample example = new PayChannelExample();
        PayChannelExample.Criteria criteria = example.createCriteria();
        criteria.andChannelIdEqualTo(channelId);
        criteria.andMchIdEqualTo(mchId);
        List<PayChannel> payChannelList = payChannelMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(payChannelList)) {
            return null;
        }
        return payChannelList.get(0);
    }

    /**
     * Description 选择商户通知
     *
     * @param orderId 订单id
     * @return
     */
    public MchNotify baseSelectMchNotify(String orderId) {
        return mchNotifyMapper.selectByPrimaryKey(orderId);
    }

    /**
     * Description 商户通知入库
     *
     * @param
     * @return
     */
    public int baseInsertMchNotify(String orderId, String mchId, String mchOrderNo, String orderType, String notifyUrl) {
        MchNotify mchNotify = new MchNotify();
        mchNotify.setOrderId(orderId);
        mchNotify.setMchId(mchId);
        mchNotify.setMchOrderNo(mchOrderNo);
        mchNotify.setOrderType(orderType);
        mchNotify.setNotifyUrl(notifyUrl);
        return mchNotifyMapper.insertSelectiveOnDuplicateKeyUpdate(mchNotify);
    }

    /**
     * Description 批量更新商户通知-通知状态为 ：成功
     *
     * @param
     * @return
     */
    public int baseUpdateMchNotifySuccess(String orderId, String result, byte notifyCount) {
        MchNotify mchNotify = new MchNotify();
        mchNotify.setStatus(PayConstant.MCH_NOTIFY_STATUS_SUCCESS);
        mchNotify.setResult(result);
        mchNotify.setNotifyCount(notifyCount);
        mchNotify.setLastNotifyTime(new Date());
        MchNotifyExample example = new MchNotifyExample();
        MchNotifyExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List values = new LinkedList<>();
        values.add(PayConstant.MCH_NOTIFY_STATUS_NOTIFYING);
        values.add(PayConstant.MCH_NOTIFY_STATUS_FAIL);
        criteria.andStatusIn(values);
        return mchNotifyMapper.updateByExampleSelective(mchNotify, example);
    }

    /**
     * Description 批量更新商户通知-通知状态为 ：失败
     *
     * @param
     * @return
     */
    public int baseUpdateMchNotifyFail(String orderId, String result, byte notifyCount) {
        MchNotify mchNotify = new MchNotify();
        mchNotify.setStatus(PayConstant.MCH_NOTIFY_STATUS_FAIL);
        mchNotify.setResult(result);
        mchNotify.setNotifyCount(notifyCount);
        mchNotify.setLastNotifyTime(new Date());
        MchNotifyExample example = new MchNotifyExample();
        MchNotifyExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List values = new LinkedList<>();
        values.add(PayConstant.MCH_NOTIFY_STATUS_NOTIFYING);
        values.add(PayConstant.MCH_NOTIFY_STATUS_FAIL);
        return mchNotifyMapper.updateByExampleSelective(mchNotify, example);
    }


}
