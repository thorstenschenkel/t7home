package de.t7soft.android.t7home.smarthome.api;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
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
import de.t7soft.android.t7home.smarthome.api.exceptions.LoginFailedException;
import de.t7soft.android.t7home.smarthome.api.exceptions.SHTechnicalException;
import de.t7soft.android.t7home.smarthome.api.exceptions.SmartHomeSessionExpiredException;
import de.t7soft.android.t7home.smarthome.util.HttpComponentsHelper;
import de.t7soft.android.t7home.smarthome.util.InputStream2String;
import de.t7soft.android.t7home.smarthome.util.XMLUtil;

/**
 * https://code.google.com/p/smarthome-java-library/source/browse/ SmarthomeJavaLibrary/src/main/java/de/itarchitecture/
 * smarthome/api/SmartHomeSession.java
 */
public class SmartHomeSession {

	private static final String FIRMWARE_VERSION = "1.70";
	private static final String BASEREQUEST_STARTTAG = "<BaseRequest xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"{0}\" Version=\"{1}\" RequestId=\"{2}\" {3}>";
	private static final String BASEREQUEST_ENDTAG = "</BaseRequest>";

	// logon data
	private String userName;
	private String passWord;
	private String hostName;

	// IDs
	private String sessionId = "";
	private String clientId;
	private String requestId = "";

	private String currentConfigurationVersion = "";

	private ConcurrentHashMap<String, SmartHomeLocation> locations = null;
	private ConcurrentHashMap<String, ? extends LogicalDevice> windowDoorSensors = null;

	private final HttpComponentsHelper httpHelper = new HttpComponentsHelper();

	public void logon(String userName, String passWord, String hostName) throws SHTechnicalException, LoginFailedException,
			SmartHomeSessionExpiredException {
		this.userName = userName;
		this.passWord = passWord;
		this.hostName = hostName;
		initialize();
	}

	private void initialize() throws SHTechnicalException, LoginFailedException, SmartHomeSessionExpiredException {

		clientId = UUID.randomUUID().toString();

		requestId = generateRequestId();
		String passWordEncrypted = generateHashFromPassword(passWord);
		String loginData = "UserName=\"" + getUserName() + "\"";
		loginData += " Password=\"" + passWordEncrypted + "\"";
		String loginRequest = buildRequest("LoginRequest", FIRMWARE_VERSION, loginData);
		try {

			String sResponse = executeRequest(loginRequest);
			setSessionId(XMLUtil.XPathValueFromString(sResponse, "/BaseResponse/@SessionId"));
			if (getSessionId() == null || "".equals(getSessionId())) {
				throw new LoginFailedException("LoginFailed: Authentication with user:" + userName
						+ " was not possible. Session ID is empty.");
			}
			currentConfigurationVersion = XMLUtil.XPathValueFromString(sResponse,
					"/BaseResponse/@CurrentConfigurationVersion");
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

	public String refreshConfiguration() throws SmartHomeSessionExpiredException {

		String attrbutes = "SessionId=\"" + getSessionId() + "\"";
		String content = "<EntityType>Configuration</EntityType>";
		String getConfigurationRequest = buildRequest("GetEntitiesRequest", "1.60", attrbutes, content);

		String sResponse = executeRequest(getConfigurationRequest);
		Logger.getLogger(SmartHomeSession.class.getName()).log(Level.SEVERE, sResponse);
		try {
			refreshConfigurationFromInputStream(IOUtils.toInputStream(sResponse, "UTF8"));
		} catch (IOException e) {
			throw new SmartHomeSessionExpiredException(e);
		}
		return sResponse;
	}

	public void refreshConfigurationFromInputStream(InputStream is) {
		SmartHomeEntitiesXMLResponse smartHomeEntitiesXMLRes = new SmartHomeEntitiesXMLResponse(is);
		this.locations = smartHomeEntitiesXMLRes.getLocations();
		this.windowDoorSensors = smartHomeEntitiesXMLRes.getWindowDoorSensors();
	}

	private String getHostName() {
		return hostName;
	}

	private String getUserName() {
		return userName;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	private String buildRequest(String type, String version, String attributes) {
		return buildRequest(type, version, attributes, null);
	}

	private String buildRequest(String type, String version, String attributes, String content) {
		String request = MessageFormat.format(BASEREQUEST_STARTTAG, type, version, requestId, attributes);
		if (content != null) {
			request += content;
		}
		request += BASEREQUEST_ENDTAG;
		return request;
	}
}
