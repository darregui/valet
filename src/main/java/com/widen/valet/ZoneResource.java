package com.widen.valet;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Collections;
import java.util.List;

public class ZoneResource
{
	public final String name;

	public final RecordType recordType;

	public final int ttl;

	public final List<String> resourceRecords;

	ZoneResource(String name, RecordType recordType, int ttl, List<String> resourceRecords)
	{
		this.name = name;
		this.recordType = recordType;
		this.ttl = ttl;
		this.resourceRecords = Collections.unmodifiableList(resourceRecords);
	}

	public final ZoneUpdateAction createAction()
	{
		return ZoneUpdateAction.createAction(name, recordType, ttl, resourceRecords.toArray(new String[0]));
	}

	public final ZoneUpdateAction deleteAction()
	{
		return ZoneUpdateAction.deleteAction(name, recordType, ttl, resourceRecords.toArray(new String[0]));
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
