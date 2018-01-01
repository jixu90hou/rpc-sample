package org.weweb.rpc.service;

import org.weweb.rpc.annoation.RpcService;

@RpcService(TestService.class)
public class TestServiceImpl implements TestService {
	@Override
	public String print(String info) {
		return "print:"+info;
	}
}
