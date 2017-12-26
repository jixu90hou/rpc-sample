package org.weweb.netty.rpc.service;

public class SampleServiceImpl implements SampleService {
	@Override
	public int add(int a, int b) {
		return a + b;
	}

	@Override
	public String say(String info) {
		return "你好啊："+info;
	}
}
