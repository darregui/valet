package com.widen.valet.util;

import com.widen.valet.RecordType;
import org.xbill.DNS.*;

import java.net.UnknownHostException;

public class NameQueryServiceImpl implements NameQueryService
{
	private Resolver resolver;

	public NameQueryServiceImpl(String nameserver)
	{
		try
		{
			resolver = new SimpleResolver(nameserver);
			resolver.setTimeout(1);
			resolver.setTCP(true);
		}
		catch (UnknownHostException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public LookupRecord lookup(String name, RecordType type)
	{
		Lookup lookup = null;

		try
		{
			lookup = new Lookup(name, Type.value(type.name()));
		}
		catch (TextParseException e)
		{
			throw new RuntimeException(e);
		}

		lookup.setResolver(resolver);

		Record[] records = lookup.run();

		if (records == null)
		{
			return LookupRecord.DOES_NOT_EXIST();
		}

		if (records.length > 0)
		{
			Record record = records[0];

			return new LookupRecord(record.rdataToString(), (int) record.getTTL(), true);
		}

		return LookupRecord.DOES_NOT_EXIST();
	}
}
