package eu.accesa.onlinestore.service;

import com.spire.doc.Document;
import com.spire.doc.Table;

//Basic concept is Read Template/format (empty invoice), replace/add some words, generate file as pdf.
//Another library would be... using empty pdf invoice to actual invoice using pdfbox
public interface PdfGeneratorService {
    //add specific number of rows to an existing table
    //update the formulas in some cells dynamicaly
    void addRows(Table table, int rowNum);

    //write data into an existing table from the first cell of the second row
    void fillTableWithData(Table table, String[][] data);

    //write the orders purchase data into the third table in the Word template
    void writeDataToDocument(Document doc, String[][] purchaseData);
}
