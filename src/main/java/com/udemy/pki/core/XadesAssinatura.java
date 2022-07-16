package com.udemy.pki.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.udemy.pki.bean.CertificadoUdemy;

public class XadesAssinatura {

	public static byte[] assinaturaXmlBasico(byte[] data, CertificadoUdemy certificado) throws Exception {
		
		try {
			//Informa o tipo de assinatura
			XMLSignatureFactory signFact = XMLSignatureFactory.getInstance("DOM");
			Reference ref = signFact.newReference("", signFact.newDigestMethod(DigestMethod.SHA256, null),
							Collections.singletonList(
									signFact.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
							null, null);

			//Informação sobre a assinatura
			SignedInfo si = signFact.newSignedInfo(
					signFact.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,
							(C14NMethodParameterSpec) null),
					signFact.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(ref));
			
			//Certificado
			X509Certificate cert = certificado.getPublicCertificate();
	        KeyInfoFactory kif = signFact.getKeyInfoFactory();
	        List<Object> x509Content = new ArrayList<>();
	        x509Content.add(cert.getSubjectX500Principal().getName());
	        x509Content.add(cert);
	        X509Data xd = kif.newX509Data(x509Content);
	        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));
	        
	        //Cria o novo arquivo
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        dbf.setNamespaceAware(true);
	        InputStream is = new ByteArrayInputStream(data);
	        Document doc = dbf.newDocumentBuilder().parse(is);

	        //Assina o novo arquivo
	        DOMSignContext dsc = new DOMSignContext(certificado.getPrivateKey(), doc.getDocumentElement());
	        dsc.setProperty("javax.xml.crypto.dsig.cacheReference", Boolean.TRUE);
	        XMLSignature signature = signFact.newXMLSignature(si, ki, null, "AssinaturaUdemy", null);
	        signature.sign(dsc);

	        ByteArrayOutputStream novoDocumento = new ByteArrayOutputStream();
	        TransformerFactory tf = TransformerFactory.newInstance();
	        Transformer trans = tf.newTransformer();
	        trans.transform(new DOMSource(doc), new StreamResult(novoDocumento));
	        
	        return novoDocumento.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
