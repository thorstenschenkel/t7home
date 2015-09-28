package de.t7soft.android.t7home.smarthome.api;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import de.t7soft.android.t7home.smarthome.api.devices.LogicalDevice;
import de.t7soft.android.t7home.smarthome.api.devices.RoomHumiditySensor;
import de.t7soft.android.t7home.smarthome.api.devices.RoomTemperatureActuator;
import de.t7soft.android.t7home.smarthome.api.devices.RoomTemperatureSensor;
import de.t7soft.android.t7home.smarthome.api.devices.TemperatureHumidityDevice;
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

	private static final boolean FAKE = false;

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
	private ConcurrentHashMap<String, ? extends LogicalDevice> windowDoorSensors = null;
	private ConcurrentHashMap<String, TemperatureHumidityDevice> temperatureHumidityDevices = null;
	private ConcurrentHashMap<String, RoomTemperatureActuator> roomTemperatureActuators;
	private ConcurrentHashMap<String, RoomTemperatureSensor> roomTemperatureSensors;
	private ConcurrentHashMap<String, RoomHumiditySensor> roomHumiditySensors;

	private final HttpComponentsHelper httpHelper = new HttpComponentsHelper();

	public SmartHomeSession() {
		super();
	}

	public SmartHomeSession(String sessionId) {
		setSessionId(sessionId);
		if (SESSION_DATA.containsKey(sessionId)) {
			SessionData sessionData = SESSION_DATA.get(sessionId);
			requestId = sessionData.getRequestId();
			version = sessionData.getVersion();
			setHostName(sessionData.getHostName());
		}
	}

	public void logon(String userName, String passWord, String hostName) throws SHTechnicalException,
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
		String passWordEncrypted = generateHashFromPassword(passWord);
		String loginData = "UserName=\"" + getUserName() + "\"";
		loginData += " Password=\"" + passWordEncrypted + "\"";
		String loginRequest = buildRequest("LoginRequest", loginData);
		try {
			if (FAKE) {
				setSessionId("FAKE_SESSION_ID");
				return;
			}
			String sResponse = executeRequest(loginRequest);
			if (sResponse == null || "".equals(sResponse)) {
				throw new LoginFailedException("LoginFailed: Authentication with user:" + userName
						+ " was not possible. Session ID is empty.");
			}
			setSessionId(XMLUtil.XPathValueFromString(sResponse, "/BaseResponse/@SessionId"));
			if (getSessionId() == null || "".equals(getSessionId())) {
				throw new LoginFailedException("LoginFailed: Authentication with user:" + userName
						+ " was not possible. Session ID is empty.");
			}
			SessionData sessionData = new SessionData();
			sessionData.setSessionId(getSessionId());
			sessionData.setRequestId(requestId);
			sessionData.setHostName(getHostName());
			sessionData.setVersion(XMLUtil.XPathValueFromString(sResponse, "/BaseResponse/@Version"));
			// currentConfigurationVersion = XMLUtil.XPathValueFromString(sResponse,
			// "/BaseResponse/@CurrentConfigurationVersion");
			SESSION_DATA.put(getSessionId(), sessionData);
		} catch (ParserConfigurationException ex) {
			Logger.getLogger(SmartHomeSession.class.getName()).log(Level.SEVERE, null, ex);
			throw new SHTechnicalException("ParserConfigurationException:" + ex.getMessage(), ex);
		} catch (SAXException ex) {
			Logger.getLogger(SmartHomeSession.class.getName()).log(Level.SEVERE, null, ex);
			throw new SHTechnicalException("SAXException:" + ex.getMessage(), ex);
		} catch (XPathExpressionException ex) {
			Logger.getLogger(SmartHomeSession.class.getName()).log(Level.SEVERE, null, ex);
			throw new SHTechnicalException("XPathExpressionException:" + ex.getMessage(), ex);
		} catch (IOException ex) {
			Logger.getLogger(SmartHomeSession.class.getName()).log(Level.SEVERE, null, ex);
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
	private String generateHashFromPassword(String plainPassword) {

		String sReturn = "";

		try {

			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(plainPassword.getBytes());
			byte byteData[] = md.digest();
			// byte[] byteData = md.digest(plainPassword.getBytes()); // Missing
			// charset

			sReturn = new String(Base64.encodeBase64(byteData));

		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(SmartHomeSession.class.getName()).log(Level.SEVERE, null, ex);
		}

		return sReturn;

	}

	/**
	 * Destroy.
	 */
	public void destroy() {
		String attributes = "SessionId=\"" + getSessionId() + "\"";
		String logoutrequest = buildRequest("LogoutRequest", attributes);
		try {
			executeRequest(logoutrequest);
		} catch (SmartHomeSessionExpiredException e) {
		}
		if (SESSION_DATA.containsKey(sessionId)) {
			SESSION_DATA.remove(sessionId);
		}
		sessionId = "";
	}

	/**
	 * Execute request.
	 * 
	 * @param loginRequest
	 *            the login request
	 * @return the string
	 * @throws SmartHomeSessionExpiredException
	 *             the smart home session expired exception
	 */
	private String executeRequest(String loginRequest) throws SmartHomeSessionExpiredException {
		String sReturn = "";
		HttpClient httpclient = httpHelper.getNewHttpClient();
		try {
			HttpPost httpPost = new HttpPost("https://" + getHostName() + "/cmd");
			httpPost.addHeader("ClientId", clientId);
			httpPost.addHeader("Connection", "Keep-Alive");
			HttpResponse response1;
			StringEntity se = new StringEntity(loginRequest, HTTP.UTF_8);
			se.setContentType("text/xml");
			httpPost.setEntity(se);
			response1 = httpclient.execute(httpPost);
			HttpEntity entity1 = response1.getEntity();
			InputStream in = entity1.getContent();
			sReturn = InputStream2String.copyFromInputStream(in, "UTF-8");
			if (sReturn.contains("IllegalSessionId")) {
				throw new SmartHomeSessionExpiredException(sReturn);
			}
			Logger.getLogger(SmartHomeSession.class.getName()).log(Level.FINE, "XMLResponse:{0}", sReturn);
			// EntityUtils.consume(entity1);
		} catch (ClientProtocolException ex) {
			Logger.getLogger(SmartHomeSession.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(SmartHomeSession.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			// httpPost.releaseConnection();
		}
		return sReturn;

	}

	public String refreshConfiguration() throws SmartHomeSessionExpiredException, SHTechnicalException {

		String attributes = "SessionId=\"" + getSessionId() + "\"";
		String content = "<EntityType>Configuration</EntityType>";
		String getConfigurationRequest = buildRequest("GetEntitiesRequest", attributes, content);

		if (FAKE) {
			locations = new ConcurrentHashMap<String, SmartHomeLocation>();
			SmartHomeLocation location = new SmartHomeLocation();
			location.setLocationId("id1");
			location.setName("Room One");
			location.setPosition("Pos ?");
			locations.put(location.getLocationId(), location);
			location = new SmartHomeLocation();
			location.setLocationId("id2");
			location.setName("Room Two");
			location.setPosition("Pos ?");
			locations.put(location.getLocationId(), location);
			return "";
		}

		String sResponse = executeRequest(getConfigurationRequest);
		if (sResponse == null || sResponse.isEmpty()) {
			throw new SmartHomeSessionExpiredException("No response!");
		}
		Logger.getLogger(SmartHomeSession.class.getName()).log(Level.INFO, sResponse);
		try {
			currentConfigurationVersion = XMLUtil
					.XPathValueFromString(sResponse, "/BaseResponse/@ConfigurationVersion");
			refreshConfigurationFromInputStream(IOUtils.toInputStream(sResponse, "UTF8"));
		} catch (ParserConfigurationException ex) {
			Logger.getLogger(SmartHomeSession.class.getName()).log(Level.SEVERE, null, ex);
			throw new SHTechnicalException("ParserConfigurationException:" + ex.getMessage(), ex);
		} catch (SAXException ex) {
			Logger.getLogger(SmartHomeSession.class.getName()).log(Level.SEVERE, null, ex);
			throw new SHTechnicalException("SAXException:" + ex.getMessage(), ex);
		} catch (XPathExpressionException ex) {
			Logger.getLogger(SmartHomeSession.class.getName()).log(Level.SEVERE, null, ex);
			throw new SHTechnicalException("XPathExpressionException:" + ex.getMessage(), ex);
		} catch (IOException e) {
			throw new SmartHomeSessionExpiredException(e);
		}
		return sResponse;
	}

	public void refreshConfigurationFromInputStream(InputStream is) {
		SmartHomeEntitiesXMLResponse smartHomeEntitiesXMLRes = new SmartHomeEntitiesXMLResponse(is);
		this.setLocations(smartHomeEntitiesXMLRes.getLocations());
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
		String getLogicalDevicesRequest = buildRequest("GetAllLogicalDeviceStatesRequest", attributes);
		String sResponse = executeRequest(getLogicalDevicesRequest);
		if (sResponse == null || sResponse.isEmpty()) {
			throw new SmartHomeSessionExpiredException("No response!");
		}
		Logger.getLogger(SmartHomeSession.class.getName()).log(Level.INFO, sResponse);
		LogicalDeviceXMLResponse logDevXmlRes = new LogicalDeviceXMLResponse();
		logDevXmlRes.refreshLogicalDevices(IOUtils.toInputStream(sResponse), roomTemperatureActuators);
		logDevXmlRes.refreshLogicalDevices(IOUtils.toInputStream(sResponse), roomTemperatureSensors);
		logDevXmlRes.refreshLogicalDevices(IOUtils.toInputStream(sResponse), roomHumiditySensors);
		logDevXmlRes.refreshLogicalDevices(IOUtils.toInputStream(sResponse), windowDoorSensors);
		return sResponse;
	}

	private String getHostName() {
		return hostName;
	}

	private void setHostName(String hostName) {
		this.hostName = hostName;
	}

	private String getUserName() {
		return userName;
	}

	public String getSessionId() {
		return sessionId;
	}

	private void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	private String buildRequest(String type, String attributes) {
		return buildRequest(type, attributes, null);
	}

	private String buildRequest(String type, String attributes, String content) {
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

	private void setLocations(ConcurrentHashMap<String, SmartHomeLocation> locations) {
		this.locations = locations;
	}

	public ConcurrentHashMap<String, TemperatureHumidityDevice> getTemperatureHumidityDevices() {
		return temperatureHumidityDevices;
	}

	public void roomTemperatureActuatorChangeState(String deviceId, String temperature)
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
		String temperatureChangeRequest = buildRequest("SetActuatorStatesRequest", attributes, content);
		Logger.getLogger(SmartHomeSession.class.getName()).log(Level.FINE,
				"ChangingTemperature: " + temperatureChangeRequest);
		executeRequest(temperatureChangeRequest);

	}

	private class SessionData {

		private String sessionId;
		private String requestId;
		private String hostName;
		private String version;

		public String getSessionId() {
			return sessionId;
		}

		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}

		public String getRequestId() {
			return requestId;
		}

		public void setRequestId(String requestId) {
			this.requestId = requestId;
		}

		public String getHostName() {
			return hostName;
		}

		public void setHostName(String hostName) {
			this.hostName = hostName;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

	}

}
