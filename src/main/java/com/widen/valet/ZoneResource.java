package com.widen.valet;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
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

	public String getFirstResource()
	{
		return resourceRecords.iterator().next();
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

	@Override
	public boolean equals(Object obj)
	{
		ZoneResource rhs = (ZoneResource) obj;
		return new EqualsBuilder().append(name, rhs.name).append(recordType, rhs.recordType).append(ttl, rhs.ttl).append(resourceRecords, rhs.resourceRecords).isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder().append(name).append(recordType).append(ttl).append(resourceRecords).toHashCode();
	}
}
