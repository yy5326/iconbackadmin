package com.golf.model.common;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * 이메일 주소 모델
 * 
 * @author wimy
 *
 */
public class MailAddressVO implements Serializable {

	private static final long serialVersionUID = -784632262274795599L;

	private Message.RecipientType recipientType = Message.RecipientType.TO;

	private String name;

	private String address;

	private String id;

	private String domain;

	public MailAddressVO() {
	}

	public MailAddressVO(String address, String name) {
		this.address = address;
		this.name = name;
	}

	public Message.RecipientType getRecipientType() {
		return recipientType;
	}

	public void setRecipientType(Message.RecipientType recipientType) {
		this.recipientType = recipientType;
	}

	private void join() {
		if (this.id != null && this.domain != null) {
			this.address = this.id + '@' + this.domain;
		}
	}

	private void split() {
		if (this.address != null) {
			String[] sa = this.address.split("\\@");
			if (sa.length > 0) {
				this.id = sa[0];
			}
			if (sa.length > 1) {
				this.domain = sa[1];
			}
		}
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		if (this.address == null) {
			join();
		}

		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
		if (this.address.length() > 0)
			this.split();
	}

	/**
	 * @return the id
	 */
	public String getId() {

		if (this.id == null) {
			split();
		}

		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the domain
	 */
	public String getDomain() {

		if (this.domain == null) {
			split();
		}

		return this.domain;
	}

	/**
	 * @param domain
	 *            the domain to set
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Address getInternetAddress() {
		try {
			return new InternetAddress(getAddress(), getName());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			return new InternetAddress(getAddress());
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new InternetAddress();
	}
}
