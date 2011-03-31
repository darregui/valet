package com.widen.valet;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Collections;
import java.util.List;

public class Zone
{
	public final String zoneId;

	public final String name;

	public final String callerReference;

	public final String comment;

	public final List<String> nameServers;

	Zone(String zoneId, String name, String callerReference, String comment, List<String> nameServers)
	{
		this.zoneId = zoneId;
		this.name = name;
		this.callerReference = callerReference;
		this.comment = comment;
		this.nameServers = Collections.unmodifiableList(nameServers);
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
