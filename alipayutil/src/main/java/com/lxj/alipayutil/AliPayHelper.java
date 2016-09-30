package com.lxj.alipayutil;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;


/**
 * Created by lxj on 2016/9/21.
 */

public class AliPayHelper {
    // 商户PID，或者合作者id
    public static String PARTNER = "";
    // 商户收款支付宝账号,就是公司账号
    public static String SELLER = "";

    private static Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 初始化合作者id和收款方支付宝账号
     * @param parterid 合作者id
     * @param sellerid 收款方支付宝账号
     */
    public static void init(String parterid,String sellerid){
        PARTNER = parterid;
        SELLER = sellerid;
    }
    /**
     * 支付的方法
     *
     * @param context
     * @param subject  商品的名称
     * @param body     商品的详细描述
     * @param price    商品的价格
     * @param orderNum 订单号
     * @param sign     私钥签名的信息，需要从服务器获取
     * @param callback 支付结果的回调接口
     */
    public static void pay(final Context context, String subject, String body, String price, String orderNum, String sign
            , final PayResultCallback callback) {
        if(TextUtils.isEmpty(SELLER) || TextUtils.isEmpty(PARTNER)){
            Toast.makeText(context,"合作者id或者商户支付宝账号不能为空！",Toast.LENGTH_SHORT).show();
            return;
        }

        //1.拼接订单信息
        String orderInfo = getOrderInfo(subject, body, price, orderNum);
        //2.拼接完整的请求参数信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
        //3.发起支付
        new Thread() {
            @Override
            public void run() {
                super.run();
                // 1.构造PayTask 对象
                PayTask alipay = new PayTask((Activity) context);
                // 2.调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);
                //3.将结果解析为java bean
                final PayResult payResult = new PayResult(result);


                //4. 在UI线程回调结果
                if (callback != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(payResult.getResultStatus().equals(PayResult.SUCCESS)){
                                //支付成功
                                callback.onSuccess(payResult);
                            }else if (payResult.getResultStatus().equals(PayResult.WAIT_CONFIRM)) {
                                //支付确认中
                                callback.onConfirming(payResult);
                            }else if(payResult.getResultStatus().equals(PayResult.CANCEL)){
                                //支付取消
                                callback.onCancel(payResult);
                            }else {
                                //支付失败
                                callback.onFail(payResult);
                            }

                        }
                    });
                }

            }
        }.start();

    }

    /**
     * create the order info. 创建订单信息
     */
    public static String getOrderInfo(String subject, String body, String price, String orderNum) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";
        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";
        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + orderNum + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    private static String getSignType() {
        return "sign_type=\"RSA\"";
    }

    public interface PayResultCallback {
        void onSuccess(PayResult payResult);

        void onConfirming(PayResult payResult);

        void onCancel(PayResult payResult);

        void onFail(PayResult payResult);
    }
}
