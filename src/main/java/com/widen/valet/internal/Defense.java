package com.widen.valet.internal;

import org.apache.commons.lang.StringUtils;

public class Defense
{

	public static void notBlank(String s, String parameter)
	{
		if (StringUtils.isBlank(s))
		{
			throw new IllegalArgumentException(String.format("Parameter %s cannot be blank.", parameter));
		}
	}

	public static void notNull(Object o, String parameter)
	{
		if (o == null)
		{
			throw new IllegalArgumentException(String.format("Parameter %s cannot be null.", parameter));
		}
	}

}
