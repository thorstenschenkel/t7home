package de.t7soft.android.t7home.smarthome.api;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
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

	public void refreshLogicalDevices(InputStream is,
			ConcurrentHashMap<String, ? extends LogicalDevice> logicalDevices) {
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

	private LogicalDevice refreshLogicalDevice(Element devEl,
			LogicalDevice logicalDevice) {

		String sType = getTextValueFromAttribute(devEl, "xsi:type");
		if (LogicalDevice.Type_RoomHumiditySensorState.equals(sType)) {
			RoomHumiditySensor roomHumiditySensor = (RoomHumiditySensor) logicalDevice;
			roomHumiditySensor
					.setLogicalDeviceType(LogicalDevice.Type_RoomHumiditySensor);
			roomHumiditySensor.setHumidity(getDoubleValueFromAttribute(devEl,
					"Humidity"));
			logicalDevice = roomHumiditySensor;
		} else if (LogicalDevice.Type_MotionDetectionSensor.equals(sType)) {
			MotionDetectionSensor motionDetectionSensor = (MotionDetectionSensor) logicalDevice;
			logicalDevice = motionDetectionSensor;
		} else if (LogicalDevice.Type_LuminanceSensor.equals(sType)) {
			LuminanceSensor luminaceSensor = (LuminanceSensor) logicalDevice;
			luminaceSensor
					.setLogicalDeviceType(LogicalDevice.Type_LuminanceSensor);
			logicalDevice = luminaceSensor;
		} else if (LogicalDevice.Type_RoomTemperatureActuatorState
				.equals(sType)) {
			RoomTemperatureActuator roomTemperatureActuator = (RoomTemperatureActuator) logicalDevice;
			roomTemperatureActuator
					.setLogicalDeviceType(LogicalDevice.Type_RoomTemperatureActuator);
			roomTemperatureActuator.setOperationMode(getTextValueFromAttribute(
					devEl, "OpnMd"));
			roomTemperatureActuator
					.setPointTemperature(getDoubleValueFromAttribute(devEl,
							"PtTmp"));
			roomTemperatureActuator
					.setWindowReductionActive(getTextValueFromAttribute(devEl,
							"WRAc"));
			logicalDevice = roomTemperatureActuator;
		} else if (LogicalDevice.Type_RoomTemperatureSensorState.equals(sType)) {
			RoomTemperatureSensor roomTemperatureSensor = (RoomTemperatureSensor) logicalDevice;
			roomTemperatureSensor
					.setLogicalDeviceType(LogicalDevice.Type_RoomTemperatureSensor);
			roomTemperatureSensor.setTemperature(getDoubleValueFromAttribute(
					devEl, "Temperature"));
			logicalDevice = roomTemperatureSensor;

		} else if (LogicalDevice.Type_SmokeDetectionSensorState.equals(sType)) {
			SwitchActuator switchActuator = (SwitchActuator) logicalDevice;
			switchActuator
					.setLogicalDeviceType(LogicalDevice.Type_SmokeDetectorSensor);
			switchActuator.setIsOn(getBooleanValueFromAttribute(devEl,
					"IsSmokeAlarm"));
			logicalDevice = switchActuator;
		} else if (LogicalDevice.Type_SwitchActuatorState.equals(sType)) {
			SwitchActuator switchActuator = (SwitchActuator) logicalDevice;
			switchActuator
					.setLogicalDeviceType(LogicalDevice.Type_SwitchActuator);
			switchActuator.setIsOn(getBooleanValueFromAttribute(devEl, "IsOn"));
			// switchActuator.setSensingBehavior(getBooleanValueFromAttribute(devEl,
			// "SensingBehavior"));
			logicalDevice = switchActuator;
		} else if (LogicalDevice.Type_WindowDoorSensorState.equals(sType)) {
			WindowDoorSensor windowDoorSensor = (WindowDoorSensor) logicalDevice;
			windowDoorSensor
					.setLogicalDeviceType(LogicalDevice.Type_WindowDoorSensor);
			windowDoorSensor.setOpen(getBooleanValueFromElements(devEl,
					"IsOpen"));
			logicalDevice = windowDoorSensor;
		} else if (LogicalDevice.Type_AlarmActuatorState.equals(sType)) {
			AlarmActuator alarmActuator = (AlarmActuator) logicalDevice;
			alarmActuator
					.setLogicalDeviceType(LogicalDevice.Type_AlarmActuator);
			alarmActuator.setOn(getBooleanValueFromAttribute(devEl, "IsOn"));
			logicalDevice = alarmActuator;
		} else if (LogicalDevice.Type_DimmerActuatorState.equals(sType)) {
			DimmerActuator dimmerActuator = (DimmerActuator) logicalDevice;
			dimmerActuator
					.setLogicalDeviceType(LogicalDevice.Type_DimmerActuator);
			dimmerActuator
					.setDimLevel(getIntValueFromAttribute(devEl, "DmLvl"));
			logicalDevice = dimmerActuator;
		} else if (LogicalDevice.Type_GenericActuatorState.equals(sType)) {
			NodeList nodes = devEl.getElementsByTagName("Ppt");
			// Puffern
			HashMap<String, String> cache = new HashMap<String, String>();
			for (int i = 0; i < nodes.getLength(); i++) {
				String name = getTextValueFromAttribute(
						(Element) nodes.item(i), "Name");
				String value = getTextValueFromAttribute(
						(Element) nodes.item(i), "Value");
				cache.put(name, value);
			}
			// Auswerten und Objekte anlegen
			if (cache.containsKey("EmailNumberAvailable")) {
				// E-Mail Actuator
				EmailActuator emailActuator = new EmailActuator();
				emailActuator
						.setLogicalDeviceType(LogicalDevice.Type_EmailActuator);
				emailActuator.setEmailAvailable(Integer.valueOf(cache
						.get("EmailNumberAvailable")));
				logicalDevice = emailActuator;
				cache.clear();
				return logicalDevice;
			}
			if (cache.containsKey("SMSNumberAvailable")) {
				// SMSActuator
				SmsActuator smsActuator = new SmsActuator();
				smsActuator
						.setLogicalDeviceType(LogicalDevice.Type_SmsActuator);
				smsActuator.setSmsAvailable(Integer.valueOf(cache
						.get("SMSNumberAvailable")));
				logicalDevice = smsActuator;
				cache.clear();
				return logicalDevice;
			}
			if (cache.containsKey("NextSunrise")) {
				// DaySensor
				DaySensor daySensor = new DaySensor();
				daySensor.setType(LogicalDevice.Type_DaySensor);
				DateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSSSZ", Locale.ENGLISH);
				try {
					daySensor.setNextSunrise(df.parse(cache.get("NextSunrise")
							.replace("000+", "GMT+")));
					daySensor.setNextSunset(df.parse(cache.get("NextSunset")
							.replace("000+", "GMT+")));
					daySensor.setNextTimeEvent(df.parse(cache.get(
							"NextTimeEvent").replace("000+", "GMT+")));
				} catch (Exception ex) {
					Logger.getLogger(LogicalDeviceXMLResponse.class.getName())
							.log(Level.SEVERE,
									"error parsing date for DaySensor");
				}
				logicalDevice = daySensor;
				cache.clear();
				return logicalDevice;
			}
			Logger.getLogger(LogicalDeviceXMLResponse.class.getName()).log(
					Level.FINE, "ignoring unknown GenericDeviceState");
		} else {
			Logger.getLogger(LogicalDeviceXMLResponse.class.getName()).log(
					Level.INFO,
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
