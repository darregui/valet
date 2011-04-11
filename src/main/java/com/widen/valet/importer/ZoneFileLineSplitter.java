package com.widen.valet.importer;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ZoneFileLineSplitter
{
	public static void main(String[] args)
	{
		System.out.println(splitLine("@        A       10.0.0.0"));
		System.out.println(splitLine("foo   MX    10  mail-server.foo.com."));
		System.out.println(splitLine("foo   TXT    ( \"Windows DNS puts parens around txt values\""));
	}

	public static List<String> splitLine(String l)
	{
		StringBuilder[] builders = new StringBuilder[] { new StringBuilder(), new StringBuilder(), new StringBuilder() } ;

		int segment = 0;

		boolean seeking = false;

		for (int i = 0; i < l.length(); i++)
		{
			char next = l.charAt(i);

			if (Character.isWhitespace(next) && segment < 2)
			{
				if (builders[segment].toString().length() > 0 && builders[segment + 1].toString().length() == 0)
				{
					seeking = true;
					continue;
				}
			}
			else
			{
				if (seeking)
				{
					seeking = false;
					segment++;
				}

				builders[segment].append(next);
			}
		}

		List<String> list = new ArrayList<String>();
		list.add(cleanup(builders[0]));
		list.add(cleanup(builders[1]));
		list.add(cleanup(builders[2]));

		return list;
	}

	private static String cleanup(StringBuilder sb)
	{
		String s = sb.toString();
		s = s.trim();
		s = s.replace("\t", " ");
		s = StringUtils.stripStart(s, "(");
		s = StringUtils.stripEnd(s, ")");
		s = s.trim();
		return s;
	}
}
