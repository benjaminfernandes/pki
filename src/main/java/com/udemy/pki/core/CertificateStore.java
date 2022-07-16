package com.udemy.pki.core;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.udemy.pki.bean.CertificadoUdemy;

public class CertificateStore {

	public static CertificadoUdemy getCertificateFromFile(String path, String key) {
		
		try {
			
			//PKCS12 tipo de certificado que contém a chave privada
			KeyStore jks = KeyStore.getInstance("PKCS12");
			InputStream in = new FileInputStream(path);
			jks.load(in, key.toCharArray());
			in.close();
			
			//Pega o alias do certificado
			String aliasJks = jks.aliases().nextElement();
			//Busca a chave privada
			PrivateKey pk = (PrivateKey) jks.getKey(aliasJks, key.toCharArray());
			//armazena a cadeia de certificação
			Certificate[] chain = jks.getCertificateChain(aliasJks);
			//pega o primeiro certificado da cadeia de certificados, certificado > autoridade certificadora > autoridade raiz
			X509Certificate publicCertificate = (X509Certificate) chain[0];
			
			return new CertificadoUdemy(pk, chain, publicCertificate.getSubjectDN().getName(), publicCertificate);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Não foi possível capturar o certificado digital");
		}
		
	}
	
	//Listar os certificados armazenados no windows
	public static List<CertificadoUdemy> listCertificateFromStore() {
		List<CertificadoUdemy> listCertificadoUdemy = new ArrayList<>();
		try {
			
			KeyStore jks = KeyStore.getInstance("Windows-MY","SunMSCAPI");
			jks.load(null, null);
			
			Enumeration<String> en = jks.aliases();
			
			while(en.hasMoreElements()) {
				String alias = (String) en.nextElement();
				
				PrivateKey pk = (PrivateKey) jks.getKey(alias, null);
				Certificate[] chain = jks.getCertificateChain(alias);
				X509Certificate publicCertificate = (X509Certificate) chain[0];
				
				listCertificadoUdemy.add(new CertificadoUdemy(pk, chain, alias, publicCertificate));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return listCertificadoUdemy;
	}
	
}
