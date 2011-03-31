package com.widen.valet.internal;

public interface Route53Pilot
{

	String executeResourceRecordSetGet(String zone);

	String executeResourceRecordSetsPost(String zone, String payload);

	String executeHostedZoneGet();

	String executeHostedZoneGet(String zone);

	String executeHostedZonePost(String payload);

	String executeChangeInfoGet(String changeId);

}
