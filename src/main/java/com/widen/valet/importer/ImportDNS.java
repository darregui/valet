package com.widen.valet.importer;

import com.widen.valet.*;
import com.widen.valet.util.ListUtil;
import com.widen.valet.util.NameQueryByRoute53APIService;
import com.widen.valet.util.NameQueryService;
import com.widen.valet.util.NameQueryServiceImpl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ImportDNS
{
	private final Logger log = LoggerFactory.getLogger(ImportDNS.class);

	private final String awsAccessKey;

	private final String awsPrivateKey;

	private final String importFile;

	private final String route53ZoneId;

	private final int defaultTTL;

	private final boolean dryRun;

	private final String nameServer;

	private NameQueryService queryService;

	public static void main(String[] args) throws IOException
	{
		Properties properties = new Properties();

		InputStream stream = ImportDNS.class.getResourceAsStream("importdns.properties");

		if (stream == null)
		{
			throw new RuntimeException("File importdns.properties not found!");
		}

		properties.load(stream);

		new ImportDNS(properties).run();
	}

	public ImportDNS(Properties properties)
	{
		awsAccessKey = getAndVerifyProperty("widen.valet.aws-access-key", properties);
		awsPrivateKey = getAndVerifyProperty("widen.valet.aws-private-key", properties);
		importFile = "zones/" + getAndVerifyProperty("widen.valet.import-file", properties);
		route53ZoneId = getAndVerifyProperty("widen.valet.aws-route53-zone-id", properties);
		defaultTTL = Integer.parseInt(getAndVerifyProperty("widen.valet.default-ttl", properties));
		dryRun = Boolean.parseBoolean(getAndVerifyProperty("widen.valet.dry-run", properties));
		nameServer = getAndVerifyProperty("widen.valet.aws-name-server", properties);
	}

	private String getAndVerifyProperty(String key, Properties properties)
	{
		String prop = properties.getProperty(key);

		if (StringUtils.isBlank(prop))
		{
			throw new RuntimeException("Property key " + key + " cannot be blank.");
		}

		return prop;
	}

	private void run() throws IOException
	{
		Route53Driver driver = new Route53Driver(awsAccessKey, awsPrivateKey);

		Zone zone = driver.zoneDetails(route53ZoneId);

		if ("route53rrs".equals(nameServer))
		{
			queryService = new NameQueryByRoute53APIService(driver, zone);
		}
		else
		{
			queryService = new NameQueryServiceImpl(nameServer);
		}

		List<ZoneUpdateAction> actions = createZoneUpdateActions(zone);

		if (dryRun)
		{
			log.info("Dry run complete.");
			return;
		}

		int batch = 0;

		final int MAX_CHANGE_RECORDS_ALLOWED = 100;

		for (List<ZoneUpdateAction> processingList : ListUtil.split(actions, MAX_CHANGE_RECORDS_ALLOWED))
		{
			batch++;

			ZoneChangeStatus sync = driver.updateZone(zone, "Sync zone records from file" + importFile + " batch " + batch, processingList);

			driver.waitForSync(sync);
		}

		log.info("Zone import complete and INSYNC!");
	}

	private List<ZoneUpdateAction> createZoneUpdateActions(Zone zone) throws IOException
	{
		List<String> lines = IOUtils.readLines(getClass().getResourceAsStream(importFile));

		boolean foundZoneRecords = false;

		List<ZoneUpdateAction> actions = new ArrayList<ZoneUpdateAction>();

		for (String l : lines)
		{
			if (!foundZoneRecords)
			{
				if (StringUtils.contains(l, ";  Zone records"))
				{
					foundZoneRecords = true;
				}

				continue;
			}

			if (StringUtils.isNotBlank(l) && !l.startsWith(";"))
			{
				log.debug("processing line: {}", l);

				actions.addAll(parseRecord(l, zone.name));
			}
		}

		log.info("Zone Update Actions Created:\n{}", StringUtils.join(actions, "\n"));

		return actions;
	}

	private List<ZoneUpdateAction> parseRecord(String record, String zone)
	{
		String[] split = StringUtils.split(record);

		if (split.length != 3)
		{
			log.warn("Ignoring the record '{}' because it is not 3 tokens.", record);
			return Collections.emptyList();
		}

		final String name = String.format("%s.%s", split[0], zone).toLowerCase();
		final RecordType type = RecordType.valueOf(split[1]);
		final String value = split[2].toLowerCase();

		NameQueryService.LookupRecord lookupRecord = queryService.lookup(name, type);

		if (!lookupRecord.exists)
		{
			return Arrays.asList(ZoneUpdateAction.createAction(name, type, defaultTTL, value));
		}
		else if (StringUtils.equals(lookupRecord.value, value))
		{
			log.debug("{} {} {} is current", new Object[] { name, type, lookupRecord.value });

			return Collections.emptyList();
		}
		else
		{
			ZoneUpdateAction delete = ZoneUpdateAction.deleteAction(name, type, lookupRecord.ttl, lookupRecord.value);

			ZoneUpdateAction create = ZoneUpdateAction.createAction(name, type, defaultTTL, value);

			return Arrays.asList(delete, create);
		}

	}

}
