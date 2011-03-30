package com.widen.valet53;

import com.mycila.xmltool.XMLDoc;
import com.mycila.xmltool.XMLTag;
import com.widen.valet53.internal.Defense;
import com.widen.valet53.internal.Route53Pilot;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.TimeZone.*;

public class Route53Driver
{
	private Logger log = LoggerFactory.getLogger(Route53Driver.class);

	private static final String ROUTE53_XML_NAMESPACE = "https://route53.amazonaws.com/doc/2010-10-01/";

	private final Route53Pilot pilot;

	public Route53Driver(Route53Pilot pilot)
	{
		this.pilot = pilot;
	}

	public ZoneChangeStatus updateZone(final Zone zone, final String comment, ZoneUpdateAction... actions)
	{
		return updateZone(zone, comment, Arrays.asList(actions));
	}

	public ZoneChangeStatus updateZone(final Zone zone, final String comment, final List<ZoneUpdateAction> updateActions)
	{
		String commentXml = StringUtils.defaultIfEmpty(comment, String.format("Modify %s records.", updateActions.size()));

		XMLTag xml = XMLDoc.newDocument(false)
				.addDefaultNamespace(ROUTE53_XML_NAMESPACE)
				.addRoot("ChangeResourceRecordSetsRequest")
				.addTag("ChangeBatch")
				.addTag("Comment").addText(commentXml)
				.addTag("Changes");

		String ns = xml.getPefix(ROUTE53_XML_NAMESPACE);

		for (ZoneUpdateAction updateAction : updateActions)
		{
			updateAction.addChangeTag(xml);
			xml.gotoTag("//%s:Changes", ns);
		}

		String payload = xml.toString();

		log.debug("Update Zone Post Payload:\n{}", payload);

		String responseText = pilot.executeResourceRecordSetsPost(zone.zoneId, payload);

		XMLTag result = XMLDoc.from(responseText, true);

		log.debug("Update Zone Response:\n{}", result);

		return parseChangeResourceRecordSetsResponse(zone.zoneId, result);
	}

	public ZoneChangeStatus queryChangeStatus(ZoneChangeStatus oldStatus)
	{
		String response = pilot.executeChangeInfoGet(oldStatus.changeId);

		XMLTag xml = XMLDoc.from(response, true);

		return parseChangeResourceRecordSetsResponse(oldStatus.zoneId, xml);
	}

	private ZoneChangeStatus parseChangeResourceRecordSetsResponse(String zoneId, XMLTag xml)
	{
		XMLTag changeInfo = xml.gotoChild("ChangeInfo");

		String changeId = StringUtils.substringAfter(changeInfo.getText("Id"), "/change/");

		ZoneChangeStatus.Status status = ZoneChangeStatus.Status.valueOf(changeInfo.getText("Status"));

		Date date = null;

		try
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			format.setTimeZone(getTimeZone("Zulu"));
			date = format.parse(changeInfo.getText("SubmittedAt"));
		}
		catch (ParseException e)
		{
			throw new RuntimeException(e);
		}

		return new ZoneChangeStatus(zoneId, changeId, status, date);
	}

	public void waitForSync(ZoneChangeStatus oldStatus)
	{
		boolean inSync = false;

		while (!inSync)
		{
			ZoneChangeStatus current = queryChangeStatus(oldStatus);

			if (ZoneChangeStatus.Status.INSYNC.equals(current.status))
			{
				inSync = true;

				log.debug("Zone {} in sync: {}", inSync);
			}
			else
			{
				try
				{
					log.debug("Sleeping while waiting for INSYNC.");
					Thread.sleep(2000);
				}
				catch (InterruptedException e)
				{
				}
			}
		}
	}

	public List<ZoneResource> listZoneRecords(final Zone zone)
	{
		String result = pilot.executeResourceRecordSetGet(zone.zoneId);

		XMLTag xml = XMLDoc.from(result, true);

		List<ZoneResource> zoneResources = new ArrayList<ZoneResource>();

		for (XMLTag record : xml.getChilds("//ResourceRecordSet"))
		{
			String name = record.getText("Name");
			String type = record.getText("Type");
			String ttl = record.getText("TTL");

			List<String> values = new ArrayList<String>();

			for (XMLTag resource : record.getChilds("ResourceRecords/ResourceRecord"))
			{
				values.add(resource.getText("Value"));
			}

			zoneResources.add(new ZoneResource(name, RecordType.valueOf(type), Integer.parseInt(ttl), values));
		}

		return zoneResources;
	}

	public List<Zone> listZones()
	{
		String result = pilot.executeHostedZoneGet("");

		XMLTag xml = XMLDoc.from(result, true);

		ArrayList<Zone> zones = new ArrayList<Zone>();

		for (XMLTag tag : xml.getChilds("//HostedZone"))
		{
			zones.add(buildZone(tag));
		}

		return zones;
	}

	public Zone zoneDetails(final String zoneId)
	{
		Zone zone = new Zone(zoneId, "", "", "", Collections.<String>emptyList());

		return zoneDetails(zone);
	}

	public Zone zoneDetails(final Zone zone)
	{
		String result = pilot.executeHostedZoneGet(zone.zoneId);

		XMLTag xml = XMLDoc.from(result, true);

		return buildZone(xml.gotoChild("HostedZone"));
	}

	private Zone buildZone(final XMLTag xml)
	{
		String id = StringUtils.substringAfter(xml.getText("Id"), "/hostedzone/");

		String name = xml.getText("Name");

		String callerReference = xml.getText("CallerReference");

		String comment = xml.getText("Config/Comment");

		List<String> nameServers = new ArrayList<String>();

		for (XMLTag nsTag : xml.getChilds("//NameServer"))
		{
			nameServers.add(nsTag.getTextOrCDATA());
		}
		
		return new Zone(id, name, callerReference, comment, nameServers);
	}

	/**
	 * Zone to create.
	 *
	 * Use zoneDetails() to get
	 *
	 * @param domainName
	 * @return
	 * @throws IllegalArgumentException if domainName is null, blank, starts/ends with period.
	 */
	public ZoneChangeStatus createZone(final String domainName, final String comment)
	{
		checkDomainName(domainName);

		ensureDomainNameNotAlreadyCreated(domainName);

		String payload = XMLDoc.newDocument(false)
				.addDefaultNamespace(ROUTE53_XML_NAMESPACE)
				.addRoot("CreateHostedZoneRequest")
				.addTag("Name").addText(domainName)
				.addTag("CallerReference").addText("Created by Valet53 (" + UUID.randomUUID().toString() + ")")
				.addTag("HostedZoneConfig")
				.addTag("Comment").addText(comment)
				.toString();

		log.debug("Create Zone Post Payload:\n{}", payload);

		String result = pilot.executeHostedZonePost(payload);

		XMLTag resultXml = XMLDoc.from(result, true);

		log.debug("Create Zone Response:\n{}", resultXml);

		Zone zone = buildZone(resultXml);

		resultXml.gotoRoot();

		return parseChangeResourceRecordSetsResponse(zone.zoneId, resultXml);
	}

	private void ensureDomainNameNotAlreadyCreated(String domainName)
	{
		List<Zone> zones = listZones();

		for (Zone zone : zones)
		{
			if (zone.name.equals(domainName))
			{
				throw new IllegalArgumentException("Domain name '" + domainName + "' is already hosted by Route53.");
			}
		}
	}

	public boolean zoneExists(final String domainName)
	{
		checkDomainName(domainName);

		List<Zone> zones = listZones();

		for (Zone zone : zones)
		{
			if (StringUtils.equalsIgnoreCase(domainName, zone.name))
			{
				return true;
			}
		}

		return false;
	}

	private void checkDomainName(String name)
	{
		Defense.notBlank(name, "name");

		if (StringUtils.startsWith(name, ".") || !StringUtils.endsWith(name, "."))
		{
			throw new IllegalArgumentException("Domain name '" + name + "' is invalid to create. Name can not start with a period and must end with a period.");
		}
	}

}
