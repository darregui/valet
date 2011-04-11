package com.widen.valet.util;

import com.widen.valet.RecordType;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface NameQueryService
{
	/**
	 * Perform a DNS lookup.
	 *
	 * @param name
	 * @param type
	 * @return
	 *      record value or null if name not found
	 */
	public LookupRecord lookup(String name, RecordType type);

	public class LookupRecord
	{
		public static final LookupRecord NON_EXISTENT_RECORD = new LookupRecord("", Collections.<String>emptyList(), 0, false);

		public final String name;

		public final List<String> values;

		public final int ttl;

		public final boolean exists;

		public LookupRecord(String name, List<String> values, int ttl, boolean exists)
		{
			this.name = name;
			this.values = values;
			this.ttl = ttl;
			this.exists = exists;
		}

		public String getFirstValue()
		{
			if (values.isEmpty())
			{
				return "";
			}

			return values.iterator().next();
		}

		public boolean valueEqual(String value)
		{
			return valuesEqual(Arrays.asList(value));
		}

		public boolean valuesEqual(List<String> values)
		{
			return values.equals(values);
		}

		@Override
		public String toString()
		{
			return new ToStringBuilder(this).append("name", name).append("exists", exists).append("value", values).toString();
		}
	}

}
