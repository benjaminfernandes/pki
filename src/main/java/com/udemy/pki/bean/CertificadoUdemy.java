package com.udemy.pki.bean;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class CertificadoUdemy {

	private final PrivateKey privateKey;
	private final Certificate[] certificateChain;
	private final String alias;
	private final X509Certificate publicCertificate;
	
	public CertificadoUdemy(PrivateKey privateKey, Certificate[] certificateChain, String alias,
			X509Certificate publicCertificate) {
		this.privateKey = privateKey;
		this.certificateChain = certificateChain;
		this.alias = alias;
		this.publicCertificate = publicCertificate;
	}
	public PrivateKey getPrivateKey() {
		return privateKey;
	}
	public Certificate[] getCertificateChain() {
		return certificateChain;
	}
	public String getAlias() {
		return alias;
	}
	public X509Certificate getPublicCertificate() {
		return publicCertificate;
	}
	
}
