# AlipayUtil
支付宝支付SDK的封装，只需要调用一个方法，传入参数和回调接口即可，像请求网络一样简单！

# Dependency [![](https://jitpack.io/v/li-xiaojun/AlipayUtil.svg)](https://jitpack.io/#li-xiaojun/AlipayUtil)
###Gradle
1. Add it in your root build.gradle at the end of repositories

		allprojects {
			repositories {
				...
				maven { url "https://jitpack.io" }
			}
		}
2. Add the dependency

		dependencies {
	        compile 'com.github.li-xiaojun:AlipayUtil:1.0.0'
		}
###maven
1. Add the JitPack repository to your build file
		
		<repositories>
			<repository>
			    <id>jitpack.io</id>
			    <url>https://jitpack.io</url>
			</repository>
		</repositories>
2. Add the dependency

		<dependency>
		    <groupId>com.github.li-xiaojun</groupId>
		    <artifactId>AlipayUtil</artifactId>
		    <version>1.0.0</version>
		</dependency>
# Usage(Just in 2 steps!)
1. 初始化合作者id和收款方支付宝账号

		AliPayHelper.init("","");
2. 调用支付方法，传入参数

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

# Intro
- 此封装支付方便我们进行支付宝SDK的调用，对于集成支付宝SDK的整体流程还是应该了解一下，事先配置好公钥参数，并且理解签名串应该放到服务器端做，让服务器端提供接口返回签名信息即可。

