package com.lxj.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.lxj.alipayutil.AliPayHelper;
import com.lxj.alipayutil.PayResult;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_pay).setOnClickListener(this);

        //1.初始化合作者id和sellerid
        AliPayHelper.init("","");

    }

    @Override
    public void onClick(View v) {
        //1.私钥签名串，不能放在本地做，需要让服务器提供接口返回，签名方法参考下面的sign方法
        //2.由于签名放在服务器端，而且需要订单信息参数，因此我们客户端要将订单信息传递给服务器，如何
        //生成订单信息也封装好了，调用AliPayHelper.getOrderInfo(),传入对应参数即可
        String sign = "";

        AliPayHelper.pay(this, "测试商品", "商品描述", "0.1", "12313211", sign, new AliPayHelper.PayResultCallback() {
            @Override
            public void onSuccess(PayResult payResult) {
                //支付成功
            }

            @Override
            public void onConfirming(PayResult payResult) {
                //支付确认中
            }
            @Override
            public void onCancel(PayResult payResult) {
                //支付取消
            }

            @Override
            public void onFail(PayResult payResult) {
                //支付失败
            }
        });
    }


    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param orderInfo
     *            待签名订单信息
     */
    private void sign(String orderInfo) {
        //由于签名操作是在服务器完成，因此私钥也是放在服务器端
        String RSA_PRIVATE = "支付宝私钥";
        //进行签名
        String sign = SignUtils.sign(orderInfo, RSA_PRIVATE);
        try {
            //得到签名串，然后返回给客户端
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
