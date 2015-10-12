package de.t7soft.android.t7home.smarthome.api;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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

import android.util.Log;
import de.t7soft.android.t7home.smarthome.api.devices.DaySensor;
import de.t7soft.android.t7home.smarthome.api.devices.LogicalDevice;
import de.t7soft.android.t7home.smarthome.api.devices.RollerShutterActuator;
import de.t7soft.android.t7home.smarthome.api.devices.RoomHumiditySensor;
import de.t7soft.android.t7home.smarthome.api.devices.RoomTemperatureActuator;
import de.t7soft.android.t7home.smarthome.api.devices.RoomTemperatureSensor;
import de.t7soft.android.t7home.smarthome.api.devices.WindowDoorSensor;

public class LogicalDeviceXMLResponse extends XMLResponse {

	private static final String LOGTAG = SmartHomeEntitiesXMLResponse.class.getSimpleName();

	private final String currentConfigurationVersion = "";
	private final String correspondingRequestId = "";
	private final String responseStatus = "";

	public void refreshLogicalDevices(final InputStream is,
			final ConcurrentHashMap<String, ? extends LogicalDevice> logicalDevices) {
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		if (null == logicalDevices)
			return;
		try {

			// Using factory get an instance of document builder
			final DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			final Document dom = db.parse(is);
			// get the root element
			final Element docEle = dom.getDocumentElement();
			// get a nodelist of elements
			final NodeList nl = docEle.getElementsByTagName("LogicalDeviceState");
			if ((nl != null) && (nl.getLength() > 0)) {
				for (int i = 0; i < nl.getLength(); i++) {

					final Element el = (Element) nl.item(i);
					final String sId = getTextValueFromAttribute(el, "LID");
					if (logicalDevices.containsKey(sId)) {
						final LogicalDevice d = logicalDevices.get(sId);
						refreshLogicalDevice(el, d);
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

	private LogicalDevice refreshLogicalDevice(final Element devEl, LogicalDevice logicalDevice) {

		final String sType = getTextValueFromAttribute(devEl, "xsi:type");
		if (LogicalDevice.Type_RoomHumiditySensorState.equals(sType)) {
			final RoomHumiditySensor roomHumiditySensor = (RoomHumiditySensor) logicalDevice;
			roomHumiditySensor.setLogicalDeviceType(LogicalDevice.Type_RoomHumiditySensor);
			roomHumiditySensor.setHumidity(getDoubleValueFromAttribute(devEl, "Humidity"));
			logicalDevice = roomHumiditySensor;
		} else if (LogicalDevice.Type_RollerShutterActuatorState.equals(sType)) {
			final RollerShutterActuator rollerShutterActuator = (RollerShutterActuator) logicalDevice;
			rollerShutterActuator.setShutterLevel(getIntValueFromElements(devEl, "ShutterLevel"));
			logicalDevice = rollerShutterActuator;
		} else if (LogicalDevice.Type_RoomTemperatureActuatorState.equals(sType)) {
			final RoomTemperatureActuator roomTemperatureActuator = (RoomTemperatureActuator) logicalDevice;
			roomTemperatureActuator.setLogicalDeviceType(LogicalDevice.Type_RoomTemperatureActuator);
			roomTemperatureActuator.setOperationMode(getTextValueFromAttribute(devEl, "OpnMd"));
			roomTemperatureActuator.setPointTemperature(getDoubleValueFromAttribute(devEl, "PtTmp"));
			roomTemperatureActuator.setWindowReductionActive(getTextValueFromAttribute(devEl, "WRAc"));
			logicalDevice = roomTemperatureActuator;
		} else if (LogicalDevice.Type_RoomTemperatureSensorState.equals(sType)) {
			final RoomTemperatureSensor roomTemperatureSensor = (RoomTemperatureSensor) logicalDevice;
			roomTemperatureSensor.setLogicalDeviceType(LogicalDevice.Type_RoomTemperatureSensor);
			roomTemperatureSensor.setTemperature(getDoubleValueFromAttribute(devEl, "Temperature"));
			logicalDevice = roomTemperatureSensor;
		} else if (LogicalDevice.Type_WindowDoorSensorState.equals(sType)) {
			final WindowDoorSensor windowDoorSensor = (WindowDoorSensor) logicalDevice;
			windowDoorSensor.setLogicalDeviceType(LogicalDevice.Type_WindowDoorSensor);
			windowDoorSensor.setOpen(getBooleanValueFromElements(devEl, "IsOpen"));
			logicalDevice = windowDoorSensor;
		} else if ("GenericDeviceState".equals(sType)) {
			final NodeList nodes = devEl.getElementsByTagName("Ppt");
			final Map<String, String> cache = new HashMap<String, String>();
			for (int i = 0; i < nodes.getLength(); i++) {
				final String name = getTextValueFromAttribute((Element) nodes.item(i), "Name");
				final String value = getTextValueFromAttribute((Element) nodes.item(i), "Value");
				cache.put(name, value);
			}
			if (cache.containsKey("NextSunrise")) {
				final DaySensor daySensor = (DaySensor) logicalDevice;
				daySensor.setType(LogicalDevice.Type_DaySensor);
				final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSZ", Locale.ENGLISH);
				try {
					daySensor.setNextSunrise(df.parse(cache.get("NextSunrise").replace("000+", "GMT+")));
					daySensor.setNextSunset(df.parse(cache.get("NextSunset").replace("000+", "GMT+")));
					daySensor.setNextTimeEvent(df.parse(cache.get("NextTimeEvent").replace("000+", "GMT+")));
				} catch (final Exception localException) {
					Logger.getLogger(LogicalDeviceXMLResponse.class.getName()).log(Level.SEVERE,
							"error parsing date for DaySensor");
				}
				cache.clear();
			}
		} else {
			final String msg = "-1-----------new/unknown sensor/actuator state: " + sType;
			Log.i(LOGTAG, msg);
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
