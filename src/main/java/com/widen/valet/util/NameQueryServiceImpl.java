package com.widen.valet.util;

import com.widen.valet.RecordType;
import org.xbill.DNS.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Query service that uses org.xbill.DNS to directly query a nameserver -- bypassing the local OS resolver.
 */
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

		if (records == null || !(records.length > 0))
		{
			return LookupRecord.NON_EXISTENT_RECORD;
		}

		int ttl = 0;

		List<String> values = new ArrayList<String>();

		for (Record r : records)
		{
			values.add(r.rdataToString());

			ttl = (int) r.getTTL();
		}

		return new LookupRecord(name, values, ttl, true);
	}
}
