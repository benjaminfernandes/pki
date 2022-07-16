package com.udemy.pki.core;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfDate;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPKCS7;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignature;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfString;
import com.udemy.pki.bean.CertificadoUdemy;

public class PadesAssinatura {

	public static byte[] assinaturaPdfBasica(byte[] data, CertificadoUdemy certificadoUdemy) throws Exception {
		
		try {
			
			PdfReader reader = new PdfReader(data);//Arquivo de entrada
			ByteArrayOutputStream novoDocumento = new ByteArrayOutputStream(); // arquivo de saída
			
			PdfStamper stp = PdfStamper.createSignature(reader, novoDocumento, '\000', null, true);
			PdfSignatureAppearance sap = stp.getSignatureAppearance();
			
			sap.setCrypto(certificadoUdemy.getPrivateKey(), certificadoUdemy.getCertificateChain(), 
					null, PdfSignatureAppearance.WINCER_SIGNED);
			sap.setReason("Assinatura digital");
			sap.setLocation("Guaíra");
			sap.setVisibleSignature(new Rectangle(100,100,350,200), 1, "sig");
			stp.close();
			
			return novoDocumento.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public static byte[] assinarPdfAvancado(byte[] dataDoc, CertificadoUdemy certificado) throws Exception {
		try {
			
			PdfReader reader = new PdfReader(dataDoc);
			ByteArrayOutputStream nuevoDocumento = new ByteArrayOutputStream();
            PdfStamper stamper = PdfStamper.createSignature(reader, nuevoDocumento, '\000', null, true);
            PdfSignatureAppearance sap = stamper.getSignatureAppearance();
            //pgcs7 não vai a chave privada
            PdfSignature signature = new PdfSignature(PdfName.ADOBE_PPKLITE, new PdfName("adbe.pkcs7.detached"));
            signature.setReason("Assinatura Digital");
            signature.setLocation("Guaíra");
            
            Date fechaFirma=new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fechaFirma);
            signature.setDate(new PdfDate(calendar));
            
            sap.setSignDate(calendar);
            sap.setCryptoDictionary(signature);
            
            String assinado = "Assinado por " + PdfPKCS7.getSubjectFields(certificado.getPublicCertificate()).getField("CN");
            String razon = "Motivo: " + "Assinatura Digital";
            String lugar = "Lugar: " + "Guaíra";
            SimpleDateFormat dateformatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss Z");
            String fecha = "Data: " + dateformatter.format(fechaFirma);
            String firmaH = assinado + '\n' + razon + '\n' + lugar + '\n' + fecha;
            sap.setLayer2Text(firmaH);
            sap.setVisibleSignature(new Rectangle(100, 100, 350, 200), 1, null);
            
            //cálculo do digest
            int contentEstimated = 8192;
            System.out.println(reader.getFileLength());
            HashMap<PdfName, Integer> exc = new HashMap<>();
            exc.put(PdfName.CONTENTS, new Integer(contentEstimated * 2 + 2));
            sap.preClose(exc);
            
            InputStream data = sap.getRangeStream();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] buf = new byte[contentEstimated];
            int n;
            while ((n = data.read(buf)) > 0) {
                messageDigest.update(buf, 0, n);
            }

            byte[] hash = messageDigest.digest();
            Calendar calendario = Calendar.getInstance();
            
            //Efetua a assinatura
            PdfPKCS7 sgn = new PdfPKCS7(certificado.getPrivateKey(), certificado.getCertificateChain(), null, "SHA-256", null, false);
            byte[] sh = sgn.getAuthenticatedAttributeBytes(hash, calendario, null);
            sgn.update(sh, 0, sh.length);
            byte[] encodedSig = sgn.getEncodedPKCS7(hash, calendario, null, null);
            
            //junta e adiciona no pdf
            byte[] paddedSig = new byte[contentEstimated];
            System.arraycopy(encodedSig, 0, paddedSig, 0, encodedSig.length);
            PdfDictionary pdfDic = new PdfDictionary();
            pdfDic.put(PdfName.CONTENTS, new PdfString(paddedSig).setHexWriting(true));
            sap.close(pdfDic);
			
            reader.close();
            nuevoDocumento.flush();
            nuevoDocumento.close();
            
			return nuevoDocumento.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
