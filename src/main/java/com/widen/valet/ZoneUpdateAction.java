package com.widen.valet;

import com.mycila.xmltool.XMLTag;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ZoneUpdateAction
{
	private final String action;

	private final String name;

	private final RecordType type;

	private final int ttl;

	private final List<String> resourceRecord;

	private ZoneUpdateAction(String action, String name, RecordType type, int ttl, List<String> resourceRecords)
	{
		this.action = action;
		this.name = name;
		this.type = type;
		this.ttl = ttl;
		this.resourceRecord = Collections.unmodifiableList(resourceRecords);
	}

	public static ZoneUpdateAction createAction(String name, RecordType type, int ttl, String... resource)
	{
		return new ZoneUpdateAction("CREATE", name, type,ttl, Arrays.asList(resource));
	}

	public static ZoneUpdateAction deleteAction(String name, RecordType type, int ttl, String... resource)
	{
		return new ZoneUpdateAction("DELETE", name, type, ttl, Arrays.asList(resource));
	}

	void addChangeTag(XMLTag xml)
	{
		xml.addTag("Change")
				.addTag("Action").addText(action)
				.addTag("ResourceRecordSet")
				.addTag("Name").addText(name)
				.addTag("Type").addText(type.name())
				.addTag("TTL").addText(String.valueOf(ttl))
				.addTag("ResourceRecords");

		for (String resource : resourceRecord)
		{
			xml.addTag("ResourceRecord").addTag("Value").addText(resource);
		}
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
