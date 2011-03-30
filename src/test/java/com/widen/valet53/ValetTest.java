package com.widen.valet53;

import com.widen.valet53.internal.Route53Pilot;
import com.widen.valet53.internal.Route53PilotMock;
import org.junit.Test;

public class ValetTest
{
	String awsAccessKey = System.getProperty("widen.valet.test-aws-access-key");

	String awsPrivate = System.getProperty("widen.valet.test-aws-secret-key");

	private Route53Pilot getPilot()
	{
		return new Route53PilotMock();

		//return new Route53PilotImpl(awsAccessKey, awsPrivate);
	}

//	@Test
//	public void testZoneInfo()
//	{
//		Zone zone = new Route53Driver(getPilot()).zoneDetails("Z18VDLRK3QY0Z4");
//
//		System.out.println(ToStringBuilder.reflectionToString(zone));
//	}
//
//
//	@Test
//	public void testListZones()
//	{
//		List<Zone> zones = new Route53Driver(new Route53PilotMock()).listZo"https://route53.amazonaws.com/doc/2010-10-01/"nes();
//
//		for (Zone z : zones)
//		{
//			System.out.println(ToStringBuilder.reflectionToString(z));
//		}
//	}

//	@Test
//	public void testAddRecords()
//	{
//		List<ZoneUpdateAction> updates = new ArrayList<ZoneUpdateAction>();
//
//		updates.add(ZoneUpdateAction.deleteAction("uriahacarpenter.com", RecordType.A, 600, "127.0.0.1"));
//		updates.add(ZoneUpdateAction.createAction("uriahacarpenter.com", RecordType.A, 600, "127.0.0.1"));
//
////		updates.add(ZoneUpdateAction.createAction("www.uriahacarpenter.com", RecordType.A, 600, "127.0.0.1"));
////		updates.add(ZoneUpdateAction.createAction("@", RecordType.MX, 600, "10 mx.googlemail.com"));
//
//		ZoneChangeStatus status = new Route53Driver(getPilot()).updateZone("Z18VDLRK3QY0Z4", null, updates);
//
//		System.out.println(status);
//	}

//	@Test
//	public void testChangeStatus()
//	{
//		ZoneChangeStatus oldStatus = new ZoneChangeStatus("C34NBUXNVUM7LE", ZoneChangeStatus.Status.PENDING, new Date());
//
//		System.out.println("old: " + oldStatus);
//
//		ZoneChangeStatus newStatus = new Route53Driver(getPilot()).queryChangeStatus(oldStatus);
//
//		System.out.println("new: " + newStatus);
//	}

//	@Test
//	public void testListRecords()
//	{
//		List<ZoneResource> resources = new Route53Driver(getPilot()).listZoneRecords("Z18VDLRK3QY0Z4");
//
//		for (ZoneResource resource : resources)
//		{
//			System.out.println(resource);
//			System.out.println(resource.createAction());
//			System.out.println(resource.deleteAction());
//		}
//
//	}

	@Test
	public void testCreateZone()
	{
		ZoneChangeStatus zone = new Route53Driver(getPilot()).createZone("uriahacarpenter.com.", "my comment");

		System.out.println(zone);
	}

}
