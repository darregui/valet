package com.widen.valet.util;

import com.widen.valet.RecordType;

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
		public final String value;

		public final int ttl;

		public final boolean exists;

		public LookupRecord(String value, int ttl, boolean exists)
		{
			this.value = value;
			this.ttl = ttl;
			this.exists = exists;
		}

		public static final LookupRecord DOES_NOT_EXIST()
		{
			return new LookupRecord("", 0, false);
		}
	}

}
