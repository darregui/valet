package com.widen.valet;

import com.widen.valet.internal.Route53Pilot;
import com.widen.valet.internal.Route53PilotMock;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ValetTest
{
	private Route53Pilot getPilot()
	{
		return new Route53PilotMock();
	}

	@Test
	public void testZoneInfo()
	{
		Zone zone = new Route53Driver(getPilot()).zoneDetails("Z18VDLRK3QY0Z4");

		System.out.println(ToStringBuilder.reflectionToString(zone));
	}

	@Test
	public void testListZones()
	{
		List<Zone> zones = new Route53Driver(getPilot()).listZones();

		for (Zone z : zones)
		{
			System.out.println(ToStringBuilder.reflectionToString(z));
		}
	}

	@Test
	public void testAddRecords()
	{
		List<ZoneUpdateAction> updates = new ArrayList<ZoneUpdateAction>();

		updates.add(ZoneUpdateAction.deleteAction("uriahacarpenter.com", RecordType.A, 600, "127.0.0.1"));
		updates.add(ZoneUpdateAction.createAction("uriahacarpenter.com", RecordType.A, 600, "127.0.0.1"));

		Zone zone = new Zone("Z1234", "uriahcarpenter.com.", "", "", Collections.<String>emptyList());

		ZoneChangeStatus status = new Route53Driver(getPilot()).updateZone(zone, "update comment", updates);

		System.out.println(status);
	}

	@Test
	public void testChangeStatus()
	{
		ZoneChangeStatus oldStatus = new ZoneChangeStatus("C34NBUXNVUM7LE", "change-id", ZoneChangeStatus.Status.PENDING, new Date());

		System.out.println("old: " + oldStatus);

		ZoneChangeStatus newStatus = new Route53Driver(getPilot()).queryChangeStatus(oldStatus);

		System.out.println("new: " + newStatus);
	}

	@Test
	public void testListRecords()
	{
		Zone zone = new Zone("Z1234", "uriahcarpenter.com.", "", "", Collections.<String>emptyList());

		List<ZoneResource> resources = new Route53Driver(getPilot()).listZoneRecords(zone);

		for (ZoneResource resource : resources)
		{
			System.out.println(resource);
			System.out.println(resource.createAction());
			System.out.println(resource.deleteAction());
		}

	}

}
