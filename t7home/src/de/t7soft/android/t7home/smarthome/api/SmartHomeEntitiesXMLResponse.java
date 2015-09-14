package de.t7soft.android.t7home.smarthome.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.t7soft.android.t7home.smarthome.api.devices.LogicalDevice;
import de.t7soft.android.t7home.smarthome.api.devices.RoomHumiditySensor;
import de.t7soft.android.t7home.smarthome.api.devices.TemperatureHumidityDevice;
import de.t7soft.android.t7home.smarthome.api.devices.WindowDoorSensor;

public class SmartHomeEntitiesXMLResponse extends XMLResponse {
	public ConcurrentHashMap<String, SmartHomeLocation> getLocations() {
		return locations;
	}

	private ConcurrentHashMap<String, SmartHomeLocation> locations = null;
	private ConcurrentHashMap<String, RoomHumiditySensor> roomHumiditySensors = null;
	private ConcurrentHashMap<String, TemperatureHumidityDevice> temperatureHumidityDevices = null;
	private ConcurrentHashMap<String, WindowDoorSensor> windowDoorSensors = null;
	private ConcurrentHashMap<String, String> mapRoomsToTemperatureActuators = null;
	private ConcurrentHashMap<String, String> mapRoomsToTemperatureSensors = null;
	private ConcurrentHashMap<String, String> mapRoomsToHumiditySensors = null;

	public SmartHomeEntitiesXMLResponse(InputStream is) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(is);
			// get the root element
			Element docEle = dom.getDocumentElement();
			// get a nodelist of elements
			// Locations
			NodeList nlLocations = docEle.getElementsByTagName("LC");
			locations = new ConcurrentHashMap<String, SmartHomeLocation>(5);
			if (nlLocations != null && nlLocations.getLength() > 0) {
				for (int i = 0; i < nlLocations.getLength(); i++) {
					Element locEl = (Element) nlLocations.item(i);
					SmartHomeLocation shl = getLocation(locEl);
					locations.put(shl.getLocationId(), shl);
				}
			}
			// LogicalDevices
			NodeList nlLogicalDevices = docEle.getElementsByTagName("LD");
			roomHumiditySensors = new ConcurrentHashMap<String, RoomHumiditySensor>();
			temperatureHumidityDevices = new ConcurrentHashMap<String, TemperatureHumidityDevice>();
			windowDoorSensors = new ConcurrentHashMap<String, WindowDoorSensor>();
			mapRoomsToTemperatureActuators = new ConcurrentHashMap<String, String>();
			mapRoomsToHumiditySensors = new ConcurrentHashMap<String, String>();
			mapRoomsToTemperatureSensors = new ConcurrentHashMap<String, String>();
			if (nlLogicalDevices != null && nlLogicalDevices.getLength() > 0) {
				for (int i = 0; i < nlLogicalDevices.getLength(); i++) {
					Element logDevEl = (Element) nlLogicalDevices.item(i);
					LogicalDevice logDev = getLogicalDevice(logDevEl);
					if (logDev != null) {
						if (!logDev.getDeviceName().equals("")) {
							Logger.getLogger(
									SmartHomeEntitiesXMLResponse.class
											.getName()).log(Level.FINEST,
									logDev.getDeviceName());
						}
						logDev.setLocation(locations.get(logDev.getLocationId()));
					}
				}
			}

		} catch (SAXException ex) {
			Logger.getLogger(LogicalDeviceXMLResponse.class.getName()).log(
					Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(LogicalDeviceXMLResponse.class.getName()).log(
					Level.SEVERE, null, ex);
		} catch (ParserConfigurationException ex) {
			Logger.getLogger(LogicalDeviceXMLResponse.class.getName()).log(
					Level.SEVERE, null, ex);
		}
	}

	private SmartHomeLocation getLocation(Element devEl) {

		// for each <SmartHomeLocation> element get text or int values
		SmartHomeLocation location = new SmartHomeLocation();
		location.setLocationId(getTextValueFromElements(devEl, "Id"));
		location.setName(getTextValueFromElements(devEl, "Name"));
		location.setPosition(getTextValueFromElements(devEl, "Position"));
		return location;
	}

	private LogicalDevice getLogicalDevice(Element devEl) {
		LogicalDevice logicalDevice = null;
		String sType = getTextValueFromAttribute(devEl, "xsi:type");
		if (LogicalDevice.Type_RoomHumiditySensor.equals(sType)) {
			RoomHumiditySensor roomHumiditySensor = new RoomHumiditySensor();
			roomHumiditySensor.setLogicalDeviceId(getTextValueFromElements(
					devEl, "Id"));
			roomHumiditySensor.setDeviceName(getTextValueFromAttribute(devEl,
					"Name"));
			roomHumiditySensor.setLocationId(getTextValueFromAttribute(devEl,
					"LCID"));

			NodeList underlyingDevNodes = devEl.getElementsByTagName("UDvIds");
			if (underlyingDevNodes != null
					&& underlyingDevNodes.getLength() > 0) {
				Element el = (Element) underlyingDevNodes.item(0);
				NodeList guidNodes = el.getElementsByTagName("guid");
				if (guidNodes != null && guidNodes.getLength() > 0) {
					for (int i = 0; i <= guidNodes.getLength(); i++) {
						// String guid = guidNodes.item(i).getNodeValue();
						mapRoomsToHumiditySensors.put(
								roomHumiditySensor.getLocationId(),
								roomHumiditySensor.getLogicalDeviceId());
					}

				}
			}

			// roomHumiditySensor.setHumidity(getDoubleValueFromElements(devEl,"Humidity"));
			logicalDevice = roomHumiditySensor;
			roomHumiditySensors.put(roomHumiditySensor.getDeviceId(),
					roomHumiditySensor);
			mapRoomsToHumiditySensors.put(roomHumiditySensor.getLocationId(),
					roomHumiditySensor.getDeviceId());
			TemperatureHumidityDevice tempHumDev = temperatureHumidityDevices
					.get(roomHumiditySensor.getLocationId());
			logicalDevice.setLocation(locations.get(logicalDevice
					.getLocationId()));
			if (null == tempHumDev) {
				tempHumDev = new TemperatureHumidityDevice();
				tempHumDev.setLocation(roomHumiditySensor.getLocation());
				temperatureHumidityDevices.put(tempHumDev.getLocationId(),
						tempHumDev);
			}
			tempHumDev.setRoomHumidtySensor(roomHumiditySensor);
		} else if (LogicalDevice.Type_WindowDoorSensor.equals(sType)) {
			WindowDoorSensor windowDoorSensor = new WindowDoorSensor();
			windowDoorSensor.setLogicalDeviceId(getTextValueFromElements(devEl,
					"Id"));
			windowDoorSensor.setDeviceName(getTextValueFromAttribute(devEl,
					"Name"));
			windowDoorSensor.setLocationId(getTextValueFromAttribute(devEl,
					"LCID"));
			windowDoorSensors.put(windowDoorSensor.getDeviceId(),
					windowDoorSensor);
			logicalDevice = windowDoorSensor;
		} else {
			logicalDevice = new LogicalDevice();
			logicalDevice.setLogicalDeviceType(LogicalDevice.Type_Generic);

			if ((!sType.contains("Sensor")) && (!sType.contains("Actuator"))) {
				Logger.getLogger(SmartHomeEntitiesXMLResponse.class.getName())
						.log(Level.INFO,
								"-2-----------new/unknown logical device: "
										+ sType);
			}
			logicalDevice.setLogicalDeviceId(getTextValueFromElements(devEl,
					"Id"));
		}

		return logicalDevice;

	}

	public ConcurrentHashMap<String, ? extends LogicalDevice> getWindowDoorSensors() {
		return windowDoorSensors;
	}

}
