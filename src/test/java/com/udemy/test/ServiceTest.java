package com.udemy.test;

import java.util.List;

import com.udemy.pki.bean.CertificadoUdemy;
import com.udemy.pki.core.CertificateStore;

public class ServiceTest {

	public static void main(String[] args) {
		
		try {
			
			/*CertificadoUdemy certificado = CertificateStore.getCertificateFromFile(Constante.CERTIFICADO, Constante.SENHA);
			
			System.out.println(certificado.getAlias());
			System.out.println("-------------");
			System.out.println(certificado.getPrivateKey().getAlgorithm());
			System.out.println("-------------");
			System.out.println(certificado.getPublicCertificate().toString());*/
			
			List<CertificadoUdemy> list = CertificateStore.listCertificateFromStore();
			list.forEach(cert -> {
				System.out.println("-----------------");
				System.out.println(cert.getAlias());
				System.out.println(cert.getPublicCertificate().getIssuerDN());
			});
			
		}catch (Exception e) {
			e.printStackTrace();
		}

	}

}
