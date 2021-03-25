package eu.accesa.onlinestore.service;

import com.spire.doc.Document;
import com.spire.doc.Table;

public interface PdfGeneratorService {
    void addRows(Table table, int rowNum);
    void fillTableWithData(Table table, String[][] data);
    void writeDataToDocument(Document doc, String[][] purchaseData);
}
