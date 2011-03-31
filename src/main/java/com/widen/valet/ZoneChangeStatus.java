package com.widen.valet;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

public class ZoneChangeStatus
{
	public enum Status
	{
		PENDING,
		INSYNC;
	}

	public final String zoneId;

	public final String changeId;

	public final Date submitDate;

	public final Status status;

	ZoneChangeStatus(String zoneId, String changeId, Status status, Date submitDate)
	{
		this.zoneId = zoneId;
		this.changeId = changeId;
		this.status = status;
		this.submitDate = submitDate;
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
