package com.mis.PdfGenerate;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

@Service
public class PdfService {
    public void generatepdf(String body, String bookingdetails,Long filename) {
        try {
            Document document=new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("C:\\Users\\DEVANSH DIXIT\\Downloads\\"+filename+".pdf"));
            document.open();
            Image img = Image.getInstance("C:\\Users\\DEVANSH DIXIT\\Pictures\\Screenshots\\Onlineseatbookingsystem.png");
            img.scaleAbsolute(540, 200);
            img.setBorder(5);
            document.add(img);
            document.add(new Paragraph(body));
            document.add(new Paragraph(bookingdetails));
            document.add(new Paragraph("\n\n\n\n In case of any query reach out to our customer care service"+"\n Email : onlineseatbookingsystem@gmail.com"));
            document.close();
            writer.close();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (
                FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (
                MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
    }

}
