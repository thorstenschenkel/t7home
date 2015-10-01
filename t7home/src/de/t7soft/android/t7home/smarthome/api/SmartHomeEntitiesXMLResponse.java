package de.t7soft.android.t7home.smarthome.api;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;
import de.t7soft.android.t7home.smarthome.api.devices.DaySensor;
import de.t7soft.android.t7home.smarthome.api.devices.LogicalDevice;
import de.t7soft.android.t7home.smarthome.api.devices.RoomHumiditySensor;
import de.t7soft.android.t7home.smarthome.api.devices.RoomTemperatureActuator;
import de.t7soft.android.t7home.smarthome.api.devices.RoomTemperatureSensor;
import de.t7soft.android.t7home.smarthome.api.devices.TemperatureHumidityDevice;
import de.t7soft.android.t7home.smarthome.api.devices.WindowDoorSensor;

public class SmartHomeEntitiesXMLResponse extends XMLResponse {

	private static final String LOGTAG = SmartHomeEntitiesXMLResponse.class.getSimpleName();

	public ConcurrentHashMap<String, SmartHomeLocation> getLocations() {
		return locations;
	}

	private ConcurrentHashMap<String, SmartHomeLocation> locations = null;
	private ConcurrentHashMap<String, RoomHumiditySensor> roomHumiditySensors = null;
	private ConcurrentHashMap<String, RoomTemperatureSensor> roomTemperatureSensors = null;
	private ConcurrentHashMap<String, TemperatureHumidityDevice> temperatureHumidityDevices = null;
	private ConcurrentHashMap<String, RoomTemperatureActuator> roomTemperatureActuators = null;
	private ConcurrentHashMap<String, WindowDoorSensor> windowDoorSensors = null;
	private ConcurrentHashMap<String, DaySensor> daySensors = null;
	private ConcurrentHashMap<String, String> mapRoomsToTemperatureActuators = null;
	private ConcurrentHashMap<String, String> mapRoomsToTemperatureSensors = null;
	private ConcurrentHashMap<String, String> mapRoomsToHumiditySensors = null;

	public SmartHomeEntitiesXMLResponse(final InputStream is) {
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			// Using factory get an instance of document builder
			final DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			final Document dom = db.parse(is);
			// get the root element
			final Element docEle = dom.getDocumentElement();
			// get a nodelist of elements
			// Locations
			final NodeList nlLocations = docEle.getElementsByTagName("LC");
			locations = new ConcurrentHashMap<String, SmartHomeLocation>(5);
			if ((nlLocations != null) && (nlLocations.getLength() > 0)) {
				for (int i = 0; i < nlLocations.getLength(); i++) {
					final Element locEl = (Element) nlLocations.item(i);
					final SmartHomeLocation shl = getLocation(locEl);
					locations.put(shl.getLocationId(), shl);
				}
			}
			// LogicalDevices
			final NodeList nlLogicalDevices = docEle.getElementsByTagName("LD");
			roomTemperatureActuators = new ConcurrentHashMap<String, RoomTemperatureActuator>();
			roomHumiditySensors = new ConcurrentHashMap<String, RoomHumiditySensor>();
			roomTemperatureSensors = new ConcurrentHashMap<String, RoomTemperatureSensor>();
			temperatureHumidityDevices = new ConcurrentHashMap<String, TemperatureHumidityDevice>();
			windowDoorSensors = new ConcurrentHashMap<String, WindowDoorSensor>();
			daySensors = new ConcurrentHashMap<String, DaySensor>();
			mapRoomsToTemperatureActuators = new ConcurrentHashMap<String, String>();
			mapRoomsToHumiditySensors = new ConcurrentHashMap<String, String>();
			mapRoomsToTemperatureSensors = new ConcurrentHashMap<String, String>();
			if ((nlLogicalDevices != null) && (nlLogicalDevices.getLength() > 0)) {
				for (int i = 0; i < nlLogicalDevices.getLength(); i++) {
					final Element logDevEl = (Element) nlLogicalDevices.item(i);
					final LogicalDevice logDev = getLogicalDevice(logDevEl);
					if (logDev != null) {
						if (!logDev.getDeviceName().equals("")) {
							Log.v(LOGTAG, logDev.getDeviceName());
						}
						logDev.setLocation(locations.get(logDev.getLocationId()));
					}
				}
			}

		} catch (final SAXException ex) {
			Log.e(LOGTAG, "", ex);
		} catch (final IOException ex) {
			Log.e(LOGTAG, "", ex);
		} catch (final ParserConfigurationException ex) {
			Log.e(LOGTAG, "", ex);
		}
	}

	private SmartHomeLocation getLocation(final Element devEl) {

		// for each <SmartHomeLocation> element get text or int values
		final SmartHomeLocation location = new SmartHomeLocation();
		location.setLocationId(getTextValueFromElements(devEl, "Id"));
		location.setName(getTextValueFromElements(devEl, "Name"));
		location.setPosition(getTextValueFromElements(devEl, "Position"));
		return location;
	}

	private LogicalDevice getLogicalDevice(final Element devEl) {
		LogicalDevice logicalDevice = null;
		final String sType = getTextValueFromAttribute(devEl, "xsi:type");
		if (LogicalDevice.Type_RoomHumiditySensor.equals(sType)) {
			final RoomHumiditySensor roomHumiditySensor = new RoomHumiditySensor();
			roomHumiditySensor.setLogicalDeviceId(getTextValueFromElements(devEl, "Id"));
			roomHumiditySensor.setDeviceName(getTextValueFromAttribute(devEl, "Name"));
			roomHumiditySensor.setLocationId(getTextValueFromAttribute(devEl, "LCID"));

			final NodeList underlyingDevNodes = devEl.getElementsByTagName("UDvIds");
			if ((underlyingDevNodes != null) && (underlyingDevNodes.getLength() > 0)) {
				final Element el = (Element) underlyingDevNodes.item(0);
				final NodeList guidNodes = el.getElementsByTagName("guid");
				if ((guidNodes != null) && (guidNodes.getLength() > 0)) {
					for (int i = 0; i <= guidNodes.getLength(); i++) {
						// String guid = guidNodes.item(i).getNodeValue();
						mapRoomsToHumiditySensors.put(roomHumiditySensor.getLocationId(),
								roomHumiditySensor.getLogicalDeviceId());
					}

				}
			}

			// roomHumiditySensor.setHumidity(getDoubleValueFromElements(devEl,"Humidity"));
			logicalDevice = roomHumiditySensor;
			roomHumiditySensors.put(roomHumiditySensor.getDeviceId(), roomHumiditySensor);
			mapRoomsToHumiditySensors.put(roomHumiditySensor.getLocationId(), roomHumiditySensor.getDeviceId());
			TemperatureHumidityDevice tempHumDev = getTemperatureHumidityDevices().get(
					roomHumiditySensor.getLocationId());
			logicalDevice.setLocation(locations.get(logicalDevice.getLocationId()));
			if (null == tempHumDev) {
				tempHumDev = new TemperatureHumidityDevice();
				tempHumDev.setLocation(roomHumiditySensor.getLocation());
				getTemperatureHumidityDevices().put(tempHumDev.getLocationId(), tempHumDev);
			}
			tempHumDev.setRoomHumiditySensor(roomHumiditySensor);
		} else if (LogicalDevice.Type_RoomTemperatureActuator.equals(sType)) {
			final RoomTemperatureActuator roomTemperatureActuator = new RoomTemperatureActuator();
			/*
			 * roomTemperatureActuator.setOperationMode(getTextValueFromElements( devEl, "OperationMode"));
			 */
			roomTemperatureActuator.setDeviceName(getTextValueFromAttribute(devEl, "Name"));
			roomTemperatureActuator.setPointTemperature(getDoubleValueFromElements(devEl, "PtTmp"));
			roomTemperatureActuator.setWindowReductionActive(getTextValueFromElements(devEl, "WndRd"));
			roomTemperatureActuator.setLogicalDeviceId(getTextValueFromElements(devEl, "Id"));
			roomTemperatureActuator.setLocationId(getTextValueFromAttribute(devEl, "LCID"));
			roomTemperatureActuator.setMaxTemperature(getDoubleValueFromElements(devEl, "MxTp"));
			roomTemperatureActuator.setMinTemperature(getDoubleValueFromElements(devEl, "MnTp"));
			roomTemperatureActuator.setPreheatFactor(getDoubleValueFromElements(devEl, "PhFct"));
			roomTemperatureActuator.setIsLocked(getBooleanValueFromElements(devEl, "Lckd"));
			roomTemperatureActuator.setIsFreezeProtectionActivated(getBooleanValueFromElements(devEl, "FPrA"));
			roomTemperatureActuator.setFreezeProtection(getDoubleValueFromElements(devEl, "FPr"));
			roomTemperatureActuator.setIsMoldProtectionActivated(getBooleanValueFromElements(devEl, "MPrA"));
			roomTemperatureActuator.setHumidityMoldProtection(getDoubleValueFromElements(devEl, "HMPr"));
			roomTemperatureActuator.setWindowsOpenTemperature(getDoubleValueFromElements(devEl, "WOpTp"));
			logicalDevice = roomTemperatureActuator;
			mapRoomsToTemperatureActuators.put(roomTemperatureActuator.getLocationId(),
					roomTemperatureActuator.getDeviceId());
			roomTemperatureActuators.put(roomTemperatureActuator.getDeviceId(), roomTemperatureActuator);
			TemperatureHumidityDevice tempHumDev = temperatureHumidityDevices.get(roomTemperatureActuator
					.getLocationId());
			logicalDevice.setLocation(locations.get(logicalDevice.getLocationId()));
			if (null == tempHumDev) {
				tempHumDev = new TemperatureHumidityDevice();
				tempHumDev.setLocation(roomTemperatureActuator.getLocation());
				temperatureHumidityDevices.put(tempHumDev.getLocationId(), tempHumDev);
			}
			tempHumDev.setTemperatureActuator(roomTemperatureActuator);
		} else if (LogicalDevice.Type_RoomTemperatureSensor.equals(sType)) {
			final RoomTemperatureSensor roomTemperatureSensor = new RoomTemperatureSensor();
			roomTemperatureSensor.setLogicalDeviceId(getTextValueFromElements(devEl, "Id"));
			roomTemperatureSensor.setDeviceName(getTextValueFromAttribute(devEl, "Name"));
			roomTemperatureSensor.setLocationId(getTextValueFromAttribute(devEl, "LCID"));
			// NodeList underlyingDevNodes = devEl
			// .getElementsByTagName("UDvIds");
			roomTemperatureSensors.put(roomTemperatureSensor.getDeviceId(), roomTemperatureSensor);
			mapRoomsToTemperatureSensors
					.put(roomTemperatureSensor.getLocationId(), roomTemperatureSensor.getDeviceId());
			logicalDevice = roomTemperatureSensor;
			TemperatureHumidityDevice tempHumDev = temperatureHumidityDevices
					.get(roomTemperatureSensor.getLocationId());
			logicalDevice.setLocation(locations.get(logicalDevice.getLocationId()));
			if (null == tempHumDev) {
				tempHumDev = new TemperatureHumidityDevice();
				tempHumDev.setLocation(roomTemperatureSensor.getLocation());
				temperatureHumidityDevices.put(tempHumDev.getLocationId(), tempHumDev);
			}
			tempHumDev.setTemperatureSensor(roomTemperatureSensor);
		} else if (LogicalDevice.Type_WindowDoorSensor.equals(sType)) {
			final WindowDoorSensor windowDoorSensor = new WindowDoorSensor();
			windowDoorSensor.setLogicalDeviceId(getTextValueFromElements(devEl, "Id"));
			windowDoorSensor.setDeviceName(getTextValueFromAttribute(devEl, "Name"));
			windowDoorSensor.setLocationId(getTextValueFromAttribute(devEl, "LCID"));
			windowDoorSensors.put(windowDoorSensor.getDeviceId(), windowDoorSensor);
			logicalDevice = windowDoorSensor;
		} else if (LogicalDevice.Type_GenericSensor.equals(sType)) {
			return getGenericSensor(devEl);
		} else {
			logicalDevice = new LogicalDevice();
			logicalDevice.setLogicalDeviceType(LogicalDevice.Type_Generic);

			if ((!sType.contains("Sensor")) && (!sType.contains("Actuator"))) {
				final String msg = "-2-----------new/unknown logical device: " + sType;
				Log.i(LOGTAG, msg);
			}
			logicalDevice.setLogicalDeviceId(getTextValueFromElements(devEl, "Id"));
		}

		return logicalDevice;

	}

	private LogicalDevice getGenericSensor(final Element devEl) {
		final NodeList nodes = devEl.getElementsByTagName("Ppt");

		final Map<String, String> cache = new HashMap<>();
		for (int i = 0; i < nodes.getLength(); i++) {
			final String name = getTextValueFromAttribute((Element) nodes.item(i), "Name");
			final String value = getTextValueFromAttribute((Element) nodes.item(i), "Value");
			cache.put(name, value);
		}
		if (cache.containsKey("NextSunrise")) {
			final DaySensor daySensor = new DaySensor();
			daySensor.setType(LogicalDevice.Type_DaySensor);
			final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSZ", Locale.ENGLISH);
			try {
				daySensor.setNextSunrise(df.parse(cache.get("NextSunrise").replace("000+", "GMT+")));
				daySensor.setNextSunset(df.parse(cache.get("NextSunset").replace("000+", "GMT+")));
				daySensor.setNextTimeEvent(df.parse(cache.get("NextTimeEvent").replace("000+", "GMT+")));
			} catch (final Exception localException) {
				Log.e(LOGTAG, "error parsing date for DaySensor");
			}
			cache.clear();
			return daySensor;
		}

		return null;
	}

	public ConcurrentHashMap<String, DaySensor> getDaySensors() {
		return daySensors;
	}

	public ConcurrentHashMap<String, WindowDoorSensor> getWindowDoorSensors() {
		return windowDoorSensors;
	}

	public ConcurrentHashMap<String, TemperatureHumidityDevice> getTemperatureHumidityDevices() {
		return temperatureHumidityDevices;
	}

	public ConcurrentHashMap<String, RoomTemperatureSensor> getRoomTemperatureSensors() {
		return roomTemperatureSensors;
	}

	public ConcurrentHashMap<String, RoomTemperatureActuator> getRoomTemperatureActuators() {
		return roomTemperatureActuators;
	}

	public ConcurrentHashMap<String, RoomHumiditySensor> getRoomHumiditySensors() {
		return roomHumiditySensors;
	}

}
