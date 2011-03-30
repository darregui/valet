package com.widen.valet53;

import com.widen.valet53.internal.Route53Pilot;
import com.widen.valet53.internal.Route53PilotImpl;

import java.util.List;

public class ValetExample
{
	public static void main(String[] args)
	{
		new ValetExample().run();
	}

	private Route53Pilot getPilot()
	{
		return new Route53PilotImpl(System.getProperty("widen.valet.aws-user-key"), System.getProperty("widen.valet.aws-private-key"));
	}

	private void run()
	{
		Route53Driver driver = new Route53Driver(getPilot());

		//ZoneChangeStatus createStatus = driver.createZone("yden.us.", "Domain for private public usage.");

		//driver.waitForSync(createStatus);

		Zone zone = driver.zoneDetails("Z3GY6LL71SLF1F");

		System.out.println(zone);

		ZoneUpdateAction add = ZoneUpdateAction.createAction("uriah.yden.us.", RecordType.A, 600, "127.0.0.1");

		ZoneChangeStatus updateChangeStatus = driver.updateZone(zone, "add uriah", add);

		driver.waitForSync(updateChangeStatus);

		List<ZoneResource> zoneResources = driver.listZoneRecords(zone);

		for (ZoneResource zoneResource : zoneResources)
		{
			System.out.println(zoneResource);
		}
	}

}
