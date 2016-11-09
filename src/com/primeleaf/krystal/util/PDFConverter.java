/**
 * Created On 30-Jul-2015
 * Copyright 2010 by Primeleaf Consulting (P) Ltd.,
 * #29,784/785 Hendre Castle,
 * D.S.Babrekar Marg,
 * Gokhale Road(North),
 * Dadar,Mumbai 400 028
 * India
 * 
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Primeleaf Consulting (P) Ltd. ("Confidential Information").  
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Primeleaf Consulting (P) Ltd.
 */

package com.primeleaf.krystal.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;
import com.primeleaf.krystal.model.vo.Document;
import com.primeleaf.krystal.model.vo.DocumentRevision;

/**
 * This file converts given document into PDF format with or without password for viewing of document in mobile device
 * or protects with users password on download.
 * Author Rahul.Kubadia
 * @since 10.0 (2016)
 * 
 */

public class PDFConverter {

	public File getConvertedFile (DocumentRevision documentRevision, Document document, String password) throws Exception{
		File tempFile =  documentRevision.getDocumentFile();
		if("TIF".equalsIgnoreCase(document.getExtension()) || "TIFF".equalsIgnoreCase(document.getExtension())){
			try{
				tempFile = File.createTempFile("temp", ".PDF");
				com.itextpdf.text.Document pdf = new com.itextpdf.text.Document();
				PdfWriter.getInstance(pdf, new FileOutputStream(tempFile));
				pdf.open();
				pdf.setMargins(0, 0, 0, 0);
				FileInputStream fis = new FileInputStream(documentRevision.getDocumentFile());
				RandomAccessFileOrArray file = new RandomAccessFileOrArray(fis);
				int pages = TiffImage.getNumberOfPages(file);
				for (int page = 1; page <= pages; page++){
					Image img = TiffImage.getTiffImage(file, page);
					img.setAbsolutePosition(0f,0f);
					img.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
					pdf.setMargins(0, 0, 0, 0);
					pdf.add(img);
					pdf.newPage();
				}
				fis.close();
				pdf.close();
				document.setExtension("PDF");
			}catch(Exception e){
				tempFile = documentRevision.getDocumentFile();
				throw new Exception ("Unable to convert TIFF Document to PDF");
			}
		}else  if("JPG".equalsIgnoreCase(document.getExtension()) || "JPEG".equalsIgnoreCase(document.getExtension())
				|| "PNG".equalsIgnoreCase(document.getExtension()) 
				|| "BMP".equalsIgnoreCase(document.getExtension()) || "GIF".equalsIgnoreCase(document.getExtension() )){
			try{
				tempFile = File.createTempFile("temp", ".PDF");
				Image img = Image.getInstance(documentRevision.getDocumentFile().getAbsolutePath());
				com.itextpdf.text.Document pdf = new com.itextpdf.text.Document(new Rectangle(img.getWidth(), img.getHeight()),0,0,0,0);
				img.setAbsolutePosition(0f,0f);
				PdfWriter.getInstance(pdf, new FileOutputStream(tempFile));
				pdf.open();
				pdf.add(img);
				pdf.close();
				document.setExtension("PDF");
			}catch(Exception e){
				tempFile = documentRevision.getDocumentFile();
				throw new Exception ("Unable to convert Image Document to PDF");
			}
		}else  if("PDF".equalsIgnoreCase(document.getExtension())){
			tempFile = documentRevision.getDocumentFile();
		}else {
			String tempFilePath = "";
			String KRYSTAL_HOME = System.getProperty("krystal.home");
			if (KRYSTAL_HOME == null) {
				KRYSTAL_HOME = System.getProperty("user.dir");
				System.setProperty("krystal.home", KRYSTAL_HOME);
			}
			tempFilePath=KRYSTAL_HOME+File.separator+"/webapps/DMC/images/unsupport.pdf";
			tempFile = new File (tempFilePath);
		}
		return tempFile;
	}

	
}

