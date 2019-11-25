package org.mylearning.notification.sms;

public class RcpPhone {
	private String number;
	private String provider;
	private String make;
	private String model;
	private String browser;
	private String intlCode;

	public String getIntlCode() {
		return intlCode;
	}

	public void setIntlCode(String intlCode) {
		this.intlCode = intlCode;
	}

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String toXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<RcpPhone>");

		sb.append("<Number>");
		sb.append(getNumber());
		sb.append("</Number>");

		if(getProvider()!=null) {
			sb.append("<Provider>");
			sb.append(getProvider());
			sb.append("</Provider>");
		}

		if(getMake()!=null) {
			sb.append("<Make>");
			sb.append(getMake());
			sb.append("</Make>");
		}

		if(getModel()!=null) {
			sb.append("<Model>");
			sb.append(getModel());
			sb.append("</Model>");
		}

		if(getBrowser()!=null) {
			sb.append("<Browser>");
			sb.append(getBrowser());
			sb.append("</Browser>");
		}

		sb.append("</RcpPhone>");
		return sb.toString();
	}
}
