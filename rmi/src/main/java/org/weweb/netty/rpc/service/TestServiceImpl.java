package org.weweb.netty.rpc.service;

import org.weweb.netty.rpc.annoation.RpcService;

@RpcService
public class TestServiceImpl implements TestService {
	@Override
	public String print(String info) {
		return "print:"+info;
	}
}
