package com.widen.valet.util;

import com.widen.valet.RecordType;

public class NameQueryServiceTest
{

	public static void main(String[] args)
	{
		NameQueryServiceImpl queryService = new NameQueryServiceImpl("ns-329.awsdns-41.com");

		String name = "test.uriahfootest.com";

		NameQueryService.LookupRecord record = queryService.lookup(name, RecordType.A);

		if (record.exists)
		{
			System.out.println(String.format("%s = %s, ttl = %s", name, record.values, record.ttl));
		}
		else
		{
			System.out.println(name + " does not exist");
		}
	}

}
