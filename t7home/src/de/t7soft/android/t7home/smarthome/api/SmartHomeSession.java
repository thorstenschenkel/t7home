package de.t7soft.android.t7home.smarthome.api;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.xml.sax.SAXException;

import android.util.Log;
import de.t7soft.android.t7home.smarthome.api.devices.DaySensor;
import de.t7soft.android.t7home.smarthome.api.devices.LogicalDevice;
import de.t7soft.android.t7home.smarthome.api.devices.RollerShutterActuator;
import de.t7soft.android.t7home.smarthome.api.devices.RoomHumiditySensor;
import de.t7soft.android.t7home.smarthome.api.devices.RoomTemperatureActuator;
import de.t7soft.android.t7home.smarthome.api.devices.RoomTemperatureSensor;
import de.t7soft.android.t7home.smarthome.api.devices.TemperatureHumidityDevice;
import de.t7soft.android.t7home.smarthome.api.devices.WindowDoorSensor;
import de.t7soft.android.t7home.smarthome.api.exceptions.LoginFailedException;
import de.t7soft.android.t7home.smarthome.api.exceptions.SHTechnicalException;
import de.t7soft.android.t7home.smarthome.api.exceptions.SmartHomeSessionExpiredException;
import de.t7soft.android.t7home.smarthome.util.HttpComponentsHelper;
import de.t7soft.android.t7home.smarthome.util.InputStream2String;
import de.t7soft.android.t7home.smarthome.util.XMLUtil;

/**
 * https://code.google.com/p/smarthome-java-library/source/browse/SmarthomeJavaLibrary/src/main/java/de/itarchitecture/ smarthome/api/SmartHomeSession.java
 * 
 * http://www.ollie.in/rwe-smarthome-api/
 */
public class SmartHomeSession {

	private static final String LOGTAG = SmartHomeSession.class.getSimpleName();

	private static final String FIRMWARE_VERSION = "1.70";
	private static final String BASEREQUEST_STARTTAG = "<BaseRequest xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"{0}\" Version=\"{1}\" RequestId=\"{2}\" {3}>";
	private static final String BASEREQUEST_ENDTAG = "</BaseRequest>";

	private static final HashMap<String, SessionData> SESSION_DATA = new HashMap<String, SessionData>();

	// logon data
	private String userName;
	private String passWord;
	private String hostName;

	// IDs
	private String clientId;
	private String sessionId = "";
	private String requestId = "";
	private String version = "";

	private String currentConfigurationVersion = "";

	private ConcurrentHashMap<String, SmartHomeLocation> locations = null;
	private ConcurrentHashMap<String, WindowDoorSensor> windowDoorSensors = null;
	private ConcurrentHashMap<String, TemperatureHumidityDevice> temperatureHumidityDevices = null;
	private ConcurrentHashMap<String, RoomTemperatureActuator> roomTemperatureActuators;
	private ConcurrentHashMap<String, RoomTemperatureSensor> roomTemperatureSensors;
	private ConcurrentHashMap<String, RoomHumiditySensor> roomHumiditySensors;
	private ConcurrentHashMap<String, RollerShutterActuator> rollerShutterActuators = null;
	private ConcurrentHashMap<String, ? extends LogicalDevice> baseSensors = null;

	private final HttpComponentsHelper httpHelper = new HttpComponentsHelper();

	public SmartHomeSession() {
		super();
	}

	public SmartHomeSession(final String sessionId) {
		setSessionId(sessionId);
		if (SESSION_DATA.containsKey(sessionId)) {
			final SessionData sessionData = SESSION_DATA.get(sessionId);
			requestId = sessionData.getRequestId();
			version = sessionData.getVersion();
			currentConfigurationVersion = sessionData.getCurrentConfigurationVersion();
			setHostName(sessionData.getHostName());
		}
	}

	public void logon(final String userName, final String passWord, final String hostName) throws SHTechnicalException,
			LoginFailedException, SmartHomeSessionExpiredException {
		this.userName = userName;
		this.passWord = passWord;
		this.hostName = hostName;
		version = FIRMWARE_VERSION;
		initialize();
	}

	private void initialize() throws SHTechnicalException, LoginFailedException, SmartHomeSessionExpiredException {

		clientId = UUID.randomUUID().toString();

		requestId = generateRequestId();
		final String passWordEncrypted = generateHashFromPassword(passWord);
		String loginData = "UserName=\"" + getUserName() + "\"";
		loginData += " Password=\"" + passWordEncrypted + "\"";
		final String loginRequest = buildRequest("LoginRequest", loginData);
		try {
			final String sResponse = executeRequest(loginRequest, true);
			if ((sResponse == null) || "".equals(sResponse)) {
				throw new LoginFailedException("LoginFailed: Authentication with user:" + userName
						+ " was not possible. Session ID is empty.");
			}
			setSessionId(XMLUtil.XPathValueFromString(sResponse, "/BaseResponse/@SessionId"));
			if ((getSessionId() == null) || "".equals(getSessionId())) {
				throw new LoginFailedException("LoginFailed: Authentication with user:" + userName
						+ " was not possible. Session ID is empty.");
			}
			final SessionData sessionData = new SessionData();
			sessionData.setSessionId(getSessionId());
			sessionData.setRequestId(requestId);
			sessionData.setHostName(getHostName());
			sessionData.setVersion(XMLUtil.XPathValueFromString(sResponse, "/BaseResponse/@Version"));
			// currentConfigurationVersion = XMLUtil.XPathValueFromString(sResponse,
			// "/BaseResponse/@CurrentConfigurationVersion");
			SESSION_DATA.put(getSessionId(), sessionData);
		} catch (final ParserConfigurationException ex) {
			Log.e(LOGTAG, "Can't parse response", ex);
			throw new SHTechnicalException("ParserConfigurationException:" + ex.getMessage(), ex);
		} catch (final SAXException ex) {
			Log.e(LOGTAG, "Can't parse response", ex);
			throw new SHTechnicalException("SAXException:" + ex.getMessage(), ex);
		} catch (final XPathExpressionException ex) {
			Log.e(LOGTAG, "Can't parse response", ex);
			throw new SHTechnicalException("XPathExpressionException:" + ex.getMessage(), ex);
		} catch (final IOException ex) {
			Log.e(LOGTAG, "Can't parse response", ex);
			throw new SHTechnicalException("IOException. Communication with host " + hostName
					+ " was not possiblte or interrupted. " + ex.getMessage(), ex);
		}
	}

	/*
	 * Generate request id.
	 * 
	 * @return the string
	 */
	private String generateRequestId() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Generate hash from password.
	 * 
	 * @param plainPassword
	 *            the plain password
	 * @return the string
	 */
	private String generateHashFromPassword(final String plainPassword) {

		String sReturn = "";

		try {

			final MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(plainPassword.getBytes());
			final byte byteData[] = md.digest();
			// byte[] byteData = md.digest(plainPassword.getBytes()); // Missing
			// charset

			sReturn = new String(Base64.encodeBase64(byteData));

		} catch (final NoSuchAlgorithmException ex) {
			Log.e(LOGTAG, "Can't generate hash from password", ex);
		}

		return sReturn;

	}

	/**
	 * Destroy.
	 */
	public void destroy() {
		final String attributes = "SessionId=\"" + getSessionId() + "\"";
		final String logoutrequest = buildRequest("LogoutRequest", attributes);
		try {
			executeRequest(logoutrequest);
		} catch (final SmartHomeSessionExpiredException e) {
		}
		if (SESSION_DATA.containsKey(sessionId)) {
			SESSION_DATA.remove(sessionId);
		}
		sessionId = "";
	}

	private String executeRequest(final String loginRequest) throws SmartHomeSessionExpiredException {
		return executeRequest(loginRequest, false);
	}

	/**
	 * Execute request.
	 * 
	 * @param loginRequest
	 *            the login request
	 * @throws SmartHomeSessionExpiredException
	 *             the smart home session expired exception
	 */
	private String executeRequest(final String loginRequest, final boolean login)
			throws SmartHomeSessionExpiredException {

		if ((!login) && ("".equals(this.sessionId))) {
			throw new SmartHomeSessionExpiredException();
		}

		String sReturn = "";
		final HttpClient httpclient = httpHelper.getNewHttpClient();
		try {

			final HttpPost httpPost = new HttpPost("https://" + getHostName() + "/cmd");
			httpPost.addHeader("ClientId", clientId);
			httpPost.addHeader("Connection", "Keep-Alive");
			final StringEntity se = new StringEntity(loginRequest, HTTP.UTF_8);
			se.setContentType("text/xml");
			httpPost.setEntity(se);
			final HttpResponse response = httpclient.execute(httpPost);

			if (response.getStatusLine().getStatusCode() == 401) {
				Log.w(LOGTAG, "401 Unauthorized returned - Session expired!");
				this.sessionId = "";
				throw new SmartHomeSessionExpiredException(sReturn);
			}

			final HttpEntity entity1 = response.getEntity();
			final InputStream in = entity1.getContent();
			sReturn = InputStream2String.copyFromInputStream(in, "UTF-8");
			if (sReturn.contains("IllegalSessionId")) {
				throw new SmartHomeSessionExpiredException(sReturn);
			}
			Log.v(LOGTAG, "XMLResponse: " + sReturn);
			// EntityUtils.consume(entity1);
		} catch (final ClientProtocolException ex) {
			Log.e(LOGTAG, "", ex);
		} catch (final IOException ex) {
			Log.e(LOGTAG, "", ex);
		} finally {
			// httpPost.releaseConnection();
		}
		return sReturn;

	}

	public String refreshConfiguration() throws SmartHomeSessionExpiredException, SHTechnicalException {

		final String attributes = "SessionId=\"" + getSessionId() + "\"";
		final String content = "<EntityType>Configuration</EntityType>";
		final String getConfigurationRequest = buildRequest("GetEntitiesRequest", attributes, content);

		final String sResponse = executeRequest(getConfigurationRequest);
		if ((sResponse == null) || sResponse.isEmpty()) {
			throw new SmartHomeSessionExpiredException("No response!");
		}
		Log.i(LOGTAG, sResponse);
		try {
			currentConfigurationVersion = XMLUtil
					.XPathValueFromString(sResponse, "/BaseResponse/@ConfigurationVersion");
			if (SESSION_DATA.containsKey(sessionId)) {
				final SessionData sessionData = SESSION_DATA.get(sessionId);
				sessionData.setCurrentConfigurationVersion(currentConfigurationVersion);
			}
			refreshConfigurationFromInputStream(IOUtils.toInputStream(sResponse, "UTF8"));
		} catch (final ParserConfigurationException ex) {
			Log.e(LOGTAG, "Can't parse response", ex);
			throw new SHTechnicalException("ParserConfigurationException:" + ex.getMessage(), ex);
		} catch (final SAXException ex) {
			Log.e(LOGTAG, "Can't parse response", ex);
			throw new SHTechnicalException("SAXException:" + ex.getMessage(), ex);
		} catch (final XPathExpressionException ex) {
			Log.e(LOGTAG, "Can't parse response", ex);
			throw new SHTechnicalException("XPathExpressionException:" + ex.getMessage(), ex);
		} catch (final IOException ex) {
			Log.e(LOGTAG, "Can't parse response", ex);
			throw new SmartHomeSessionExpiredException(ex);
		}
		return sResponse;
	}

	public void refreshConfigurationFromInputStream(final InputStream is) {
		final SmartHomeEntitiesXMLResponse smartHomeEntitiesXMLRes = new SmartHomeEntitiesXMLResponse(is);
		this.setLocations(smartHomeEntitiesXMLRes.getLocations());
		this.baseSensors = smartHomeEntitiesXMLRes.getBaseSensors();
		this.rollerShutterActuators = smartHomeEntitiesXMLRes.getRollerShutterActuators();
		this.temperatureHumidityDevices = smartHomeEntitiesXMLRes.getTemperatureHumidityDevices();
		this.roomTemperatureActuators = smartHomeEntitiesXMLRes.getRoomTemperatureActuators();
		this.roomTemperatureSensors = smartHomeEntitiesXMLRes.getRoomTemperatureSensors();
		this.roomHumiditySensors = smartHomeEntitiesXMLRes.getRoomHumiditySensors();
		this.windowDoorSensors = smartHomeEntitiesXMLRes.getWindowDoorSensors();
	}

	public String refreshLogicalDeviceState() throws SmartHomeSessionExpiredException {
		String attributes = "SessionId=\"" + getSessionId() + "\"";
		attributes += " ";
		attributes += "BasedOnConfigVersion=\"" + currentConfigurationVersion + "\"";
		final String getLogicalDevicesRequest = buildRequest("GetAllLogicalDeviceStatesRequest", attributes);
		final String sResponse = executeRequest(getLogicalDevicesRequest);
		if ((sResponse == null) || sResponse.isEmpty()) {
			throw new SmartHomeSessionExpiredException("No response!");
		}
		Log.i(LOGTAG, sResponse);
		final LogicalDeviceXMLResponse logDevXmlRes = new LogicalDeviceXMLResponse();
		logDevXmlRes.refreshLogicalDevices(IOUtils.toInputStream(sResponse), rollerShutterActuators);
		logDevXmlRes.refreshLogicalDevices(IOUtils.toInputStream(sResponse), roomTemperatureActuators);
		logDevXmlRes.refreshLogicalDevices(IOUtils.toInputStream(sResponse), roomTemperatureSensors);
		logDevXmlRes.refreshLogicalDevices(IOUtils.toInputStream(sResponse), roomHumiditySensors);
		logDevXmlRes.refreshLogicalDevices(IOUtils.toInputStream(sResponse), windowDoorSensors);
		logDevXmlRes.refreshLogicalDevices(IOUtils.toInputStream(sResponse), baseSensors);
		return sResponse;
	}

	private String getHostName() {
		return hostName;
	}

	private void setHostName(final String hostName) {
		this.hostName = hostName;
	}

	private String getUserName() {
		return userName;
	}

	public String getSessionId() {
		return sessionId;
	}

	private void setSessionId(final String sessionId) {
		this.sessionId = sessionId;
	}

	private String buildRequest(final String type, final String attributes) {
		return buildRequest(type, attributes, null);
	}

	private String buildRequest(final String type, final String attributes, final String content) {
		String request = MessageFormat.format(BASEREQUEST_STARTTAG, type, version, requestId, attributes);
		if (content != null) {
			request += content;
		}
		request += BASEREQUEST_ENDTAG;
		return request;
	}

	public ConcurrentHashMap<String, SmartHomeLocation> getLocations() {
		return locations;
	}

	public ConcurrentHashMap<String, ? extends LogicalDevice> getGenericSensors() {
		return this.baseSensors;
	}

	private void setLocations(final ConcurrentHashMap<String, SmartHomeLocation> locations) {
		this.locations = locations;
	}

	public ConcurrentHashMap<String, TemperatureHumidityDevice> getTemperatureHumidityDevices() {
		return temperatureHumidityDevices;
	}

	public ConcurrentHashMap<String, WindowDoorSensor> getWindowDoorSensors() {
		return this.windowDoorSensors;
	}

	public ConcurrentHashMap<String, RollerShutterActuator> getRollerShutterActuators() {
		return this.rollerShutterActuators;
	}

	public DaySensor getDaySensor() {
		if (baseSensors == null) {
			return null;
		}
		final Enumeration<? extends LogicalDevice> sensors = baseSensors.elements();
		if ((sensors != null) && sensors.hasMoreElements()) {
			final LogicalDevice sensor = sensors.nextElement();
			if (sensor instanceof DaySensor) {
				return (DaySensor) sensor;
			}
		}
		return null;
	}

	public void roomTemperatureActuatorChangeState(final String deviceId, final String temperature)
			throws SmartHomeSessionExpiredException {

		String attributes = "SessionId=\"" + getSessionId() + "\"";
		attributes += " ";
		attributes += "BasedOnConfigVersion=\"" + currentConfigurationVersion + "\"";

		String content = "<ActuatorStates>";
		content += "<LogicalDeviceState xsi:type=\"RoomTemperatureActuatorState\" LID=\"";
		content += deviceId;
		content += "\" PtTmp=\"";
		content += temperature;
		content += "\" OpnMd=\"";
		content += "Auto";
		content += "\" WRAc=\"False\" />";
		content += "</ActuatorStates>";

		final String temperatureChangeRequest = buildRequest("SetActuatorStatesRequest", attributes, content);
		Log.d(LOGTAG, "ChangingTemperature: " + temperatureChangeRequest);
		executeRequest(temperatureChangeRequest);

	}

	public void switchRollerShutter(final String deviceId, final String newValue)
			throws SmartHomeSessionExpiredException {

		String attributes = "SessionId=\"" + getSessionId() + "\"";
		attributes += " ";
		attributes += "BasedOnConfigVersion=\"" + currentConfigurationVersion + "\"";

		String content = "<ActuatorStates>";
		content += "<LogicalDeviceState xsi:type=\"RollerShutterActuatorState\" LID=\"";
		content += deviceId;
		content += "\">";
		content += "<ShutterLevel>";
		content += newValue;
		content += "</ShutterLevel>";
		content += "</LogicalDeviceState>";
		content += "</ActuatorStates>";

		final String switchOnRequest = buildRequest("SetActuatorStatesRequest", attributes, content);
		Log.d(LOGTAG, "ChangingRollerShutterLevel: " + switchOnRequest);

		executeRequest(switchOnRequest);

	}

	public void subscribeForDeviceStateChanges() throws SmartHomeSessionExpiredException {
		subscribeForNotification("DeviceStateChanges");
	}

	private void subscribeForNotification(final String notificationType) throws SmartHomeSessionExpiredException {
		final String attributes = "SessionId=\"" + getSessionId() + "\"";

		String content = "<Action>";
		content += "Subscribe";
		content += "</Action>";
		content += "<NotificationType>";
		content += "notificationType";
		content += "</NotificationType>";
		final String notificationRequest = buildRequest("NotificationRequest", attributes, content);
		Log.d(LOGTAG, "REQ: " + notificationRequest);
		final String sResponse = executeRequest(notificationRequest);
		Log.d(LOGTAG, "SubscribeForNotification-Response: " + sResponse);
	}

	// TODO
	// public List<LogicalDevice> getNotifications() throws LogoutNotificationException, SmartHomeSessionExpiredException,
	// ConfigurationChangedException {
	// final String sResponse = executeRequest("upd", "/upd");
	// if ((sResponse.contains("LogoutNotification")) || (sResponse.contains("ConfigurationChangedNotification"))) {
	// new NotificationsXMLResponse(IOUtils.toInputStream(sResponse));
	// }
	// final LogicalDeviceXMLResponse logDevXmlRes = new LogicalDeviceXMLResponse();
	// final Map<String, LogicalDevice> allDevices = new ConcurrentHashMap<String, LogicalDevice>();
	// allDevices.putAll(this.rollerShutterActuators);
	// allDevices.putAll(this.roomTemperatureActuators);
	// allDevices.putAll(this.roomTemperatureSensors);
	// allDevices.putAll(this.roomHumiditySensors);
	// allDevices.putAll(this.windowDoorSensors);
	// allDevices.putAll(this.baseSensors);
	//
	// final List<LogicalDevice> changedDevices = logDevXmlRes.refreshLogicalDevices(IOUtils.toInputStream(sResponse),
	// allDevices);
	// if (changedDevices != null) {
	// Log.d(LOGTAG, Integer.valueOf(changedDevices.size()) + "{} device(s) changed." );
	// } else {
	// Log.d(LOGTAG, "no devices have changed.");
	// }
	// return changedDevices;
	// }

	private class SessionData {

		private String sessionId;
		private String requestId;
		private String hostName;
		private String version;
		private String currentConfigurationVersion;

		public String getSessionId() {
			return sessionId;
		}

		public void setSessionId(final String sessionId) {
			this.sessionId = sessionId;
		}

		public String getRequestId() {
			return requestId;
		}

		public void setRequestId(final String requestId) {
			this.requestId = requestId;
		}

		public String getHostName() {
			return hostName;
		}

		public void setHostName(final String hostName) {
			this.hostName = hostName;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(final String version) {
			this.version = version;
		}

		public String getCurrentConfigurationVersion() {
			return currentConfigurationVersion;
		}

		public void setCurrentConfigurationVersion(final String currentConfigurationVersion) {
			this.currentConfigurationVersion = currentConfigurationVersion;
		}

	}

}
