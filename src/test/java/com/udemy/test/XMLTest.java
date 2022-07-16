package com.udemy.test;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.udemy.pki.bean.CertificadoUdemy;
import com.udemy.pki.core.CertificateStore;
import com.udemy.pki.core.XadesAssinatura;
import com.udemy.pki.util.Constante;

public class XMLTest {

	public static void main(String[] args) {
		try {

			CertificadoUdemy certificado = CertificateStore.getCertificateFromFile(Constante.CERTIFICADO,
					Constante.SENHA);
			Path path = Paths.get(Constante.XML);
			byte[] documento = Files.readAllBytes(path);
			documento = XadesAssinatura.assinaturaXmlBasico(documento, certificado);

			FileOutputStream out = new FileOutputStream(Constante.XML_ASSINADO);
			out.write(documento);
			out.close();

			System.out.println("Assinatura xml com sucesso");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
