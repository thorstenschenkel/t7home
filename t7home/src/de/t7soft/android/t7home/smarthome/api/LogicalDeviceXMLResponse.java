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

public class LogicalDeviceXMLResponse extends XMLResponse {

	private final String currentConfigurationVersion = "";
	private final String correspondingRequestId = "";
	private final String responseStatus = "";

	public void refreshLogicalDevices(InputStream is, ConcurrentHashMap<String, ? extends LogicalDevice> logicalDevices) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		if (null == logicalDevices)
			return;
		try {

			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(is);
			// get the root element
			Element docEle = dom.getDocumentElement();
			// get a nodelist of elements
			NodeList nl = docEle.getElementsByTagName("LogicalDeviceState");
			if (nl != null && nl.getLength() > 0) {
				for (int i = 0; i < nl.getLength(); i++) {

					Element el = (Element) nl.item(i);
					String sId = getTextValueFromAttribute(el, "LID");
					if (logicalDevices.containsKey(sId)) {
						LogicalDevice d = logicalDevices.get(sId);
						refreshLogicalDevice(el, d);
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

	private LogicalDevice refreshLogicalDevice(Element devEl, LogicalDevice logicalDevice) {

		String sType = getTextValueFromAttribute(devEl, "xsi:type");
		if (LogicalDevice.Type_WindowDoorSensorState.equals(sType)) {
			WindowDoorSensor windowDoorSensor = (WindowDoorSensor) logicalDevice;
			windowDoorSensor.setLogicalDeviceType(LogicalDevice.Type_WindowDoorSensor);
			windowDoorSensor.setOpen(getBooleanValueFromElements(devEl, "IsOpen"));
			logicalDevice = windowDoorSensor;
		} else {
			Logger.getLogger(LogicalDeviceXMLResponse.class.getName()).log(Level.INFO,
					"-1-----------new/unknown sensor/actuator state: " + sType);
		}

		return logicalDevice;
	}

	/**
	 * @return the currentConfigurationVersion
	 */
	public String getCurrentConfigurationVersion() {
		return currentConfigurationVersion;
	}

	/**
	 * @return the correspondingRequestId
	 */
	public String getCorrespondingRequestId() {
		return correspondingRequestId;
	}

	/**
	 * @return the responseStatus
	 */
	public String getResponseStatus() {
		return responseStatus;
	}
}
