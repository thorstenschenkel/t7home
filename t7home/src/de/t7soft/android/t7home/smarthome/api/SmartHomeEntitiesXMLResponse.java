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
import de.t7soft.android.t7home.smarthome.api.devices.WindowDoorSensor;

public class SmartHomeEntitiesXMLResponse extends XMLResponse {
	public ConcurrentHashMap<String, SmartHomeLocation> getLocations() {
		return locations;
	}

	private ConcurrentHashMap<String, SmartHomeLocation> locations = null;
	private ConcurrentHashMap<String, WindowDoorSensor> windowDoorSensors = null;

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
			windowDoorSensors = new ConcurrentHashMap<String, WindowDoorSensor>();
			if (nlLogicalDevices != null && nlLogicalDevices.getLength() > 0) {
				for (int i = 0; i < nlLogicalDevices.getLength(); i++) {
					Element logDevEl = (Element) nlLogicalDevices.item(i);
					LogicalDevice logDev = getLogicalDevice(logDevEl);
					if (logDev != null) {
						if (!logDev.getDeviceName().equals("")) {
							Logger.getLogger(SmartHomeEntitiesXMLResponse.class.getName()).log(Level.FINEST,
									logDev.getDeviceName());
						}
						logDev.setLocation(locations.get(logDev.getLocationId()));
					}
				}
			}

		} catch (SAXException ex) {
			Logger.getLogger(LogicalDeviceXMLResponse.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(LogicalDeviceXMLResponse.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ParserConfigurationException ex) {
			Logger.getLogger(LogicalDeviceXMLResponse.class.getName()).log(Level.SEVERE, null, ex);
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
		if (LogicalDevice.Type_WindowDoorSensor.equals(sType)) {
			WindowDoorSensor windowDoorSensor = new WindowDoorSensor();
			windowDoorSensor.setLogicalDeviceId(getTextValueFromElements(devEl, "Id"));
			windowDoorSensor.setDeviceName(getTextValueFromAttribute(devEl, "Name"));
			windowDoorSensor.setLocationId(getTextValueFromAttribute(devEl, "LCID"));
			windowDoorSensors.put(windowDoorSensor.getDeviceId(), windowDoorSensor);
			logicalDevice = windowDoorSensor;
		} else {
			logicalDevice = new LogicalDevice();
			logicalDevice.setLogicalDeviceType(LogicalDevice.Type_Generic);

			if ((!sType.contains("Sensor")) && (!sType.contains("Actuator"))) {
				Logger.getLogger(SmartHomeEntitiesXMLResponse.class.getName()).log(Level.INFO,
						"-2-----------new/unknown logical device: " + sType);
			}
			logicalDevice.setLogicalDeviceId(getTextValueFromElements(devEl, "Id"));
		}

		return logicalDevice;

	}

	public ConcurrentHashMap<String, ? extends LogicalDevice> getWindowDoorSensors() {
		return windowDoorSensors;
	}

}
