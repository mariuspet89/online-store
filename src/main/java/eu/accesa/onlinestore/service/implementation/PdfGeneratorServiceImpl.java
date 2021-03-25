package eu.accesa.onlinestore.service.implementation;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.Table;
import com.spire.doc.fields.Field;
import eu.accesa.onlinestore.model.entity.OrderEntity;
import eu.accesa.onlinestore.service.PdfGeneratorService;
import org.springframework.stereotype.Service;

@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {
    @Override
    public void addRows(Table table, int rowNum) {
        for (int i = 0; i < rowNum; i++) {
            //insert specific number of rows by cloning the second row
            table.getRows().insert(2 + i, table.getRows().get(1).deepClone());
            //update formulas for Total
            for (Object object : table.getRows().get(2 + i).getCells().get(3).getParagraphs().get(0).getChildObjects()
            ) {
                if (object instanceof Field) {
                    Field field = (Field) object;
                    field.setCode(String.format("=B%d*C%d\\# \"0.00\"", 3 + i,3 + i));
                }
                break;
            }
        }
        //update formula for Total Tax
        for (Object object : table.getRows().get(4 + rowNum).getCells().get(3).getParagraphs().get(0).getChildObjects()
        ) {
            if (object instanceof Field) {
                Field field = (Field) object;
                field.setCode(String.format("=D%d*0.19\\# \"0.00\"", 3 + rowNum));
            }
            break;
        }
        //update formula for Balance Due
        for (Object object : table.getRows().get(5 + rowNum).getCells().get(3).getParagraphs().get(0).getChildObjects()
        ) {
            if (object instanceof Field) {
                Field field = (Field) object;
                field.setCode(String.format("=D%d+D%d\\# \"RON#,##0.00\"", 3 + rowNum, 5 + rowNum));
            }
            break;
        }
    }

    @Override
    public void fillTableWithData(Table table, String[][] data) {
        for (int r = 0; r < data.length; r++) {
            for (int c = 0; c < data[r].length; c++) {
                //fill data in cells
                table.getRows().get(r + 1).getCells().get(c).getParagraphs().get(0).setText(data[r][c]);
            }
        }
    }

    @Override
    public void writeDataToDocument(Document doc, String[][] purchaseData) {
        //get the third table
        Table table = doc.getSections().get(0).getTables().get(2);
        //determine if it needs to add rows
        if (purchaseData.length > 1) {
            //add rows
            addRows(table, purchaseData.length - 1);
        }
        //fill the table cells with value
        fillTableWithData(table, purchaseData);
    }

    public Document generateInvoice(OrderEntity order){
        //create a document instance
        Document doc= new Document();
        //load the template file
        doc.loadFromFile("C:\\Users\\dan.goia\\Desktop\\online-store\\online-store_BE\\Invoice-Template.docx");
        //replace text in the document
        doc.replace("#InvoiceNum", order.getId(), true, true);
        doc.replace("#CompanyName", order.getUser().getLastName()+" "+order.getUser().getFirstName(), true, true);
        doc.replace("#CompanyAddress", order.getUser().getAddressEntity().getAddress(), true, true);
        doc.replace("#CityStateZip", order.getUser().getAddressEntity().getCity(), true, true);
        doc.replace("#Country", order.getUser().getAddressEntity().getCounty(), true, true);
        doc.replace("#Tel1", order.getUser().getTelephone(), true, true);
        doc.replace("#ContactPerson", order.getUser().getLastName()+" "+order.getUser().getFirstName(), true, true);
        doc.replace("#ShippingAddress", order.getUser().getAddressEntity().getAddress(), true, true);
        doc.replace("#Tel2", order.getUser().getTelephone(), true, true);

        //define purchase data
        String[][] purchaseData = {
                new String[]{"Bicicleta MTB", "1", "22.8"},
                new String[]{"Bicicleta B", "4", "35.3"},
                new String[]{"Bicicleta C", "2", "52.9"},
                new String[]{"Bicicleta D", "3", "25"},
        };
        //write the purchase data to the document
        writeDataToDocument(doc, purchaseData);

        //update fields
        doc.isUpdateFields(true);

        //save file in pdf format
        doc.saveToFile("Invoice.pdf", FileFormat.PDF);
        return  doc;
    }
}
