package eu.accesa.onlinestore.service.implementation;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.Table;
import com.spire.doc.fields.Field;
import eu.accesa.onlinestore.model.entity.OrderEntity;
import eu.accesa.onlinestore.model.invoice.ProductLine;
import eu.accesa.onlinestore.service.PdfGeneratorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    /*This function actually duplicates the second row of the existing table and adds the duplicated rows successively next to the second row.
        The new rows inherit the cell format, font style as well as formula of the second row.
        Therefore, we need to update the formulas in the new rows in turn, and the following formulas that change as the number of rows increases.*/
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
                    field.setCode(String.format("=B%d*C%d\\# \"0.00\"", 3 + i, 3 + i));
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

    /*this fucntion is simply used to write data from String[][] to the table, from the first cell of the second row.*/
    @Override
    public void fillTableWithData(Table table, String[][] data) {
        for (int r = 0; r < data.length; r++) {
            for (int c = 0; c < data[r].length; c++) {
                //fill data in cells
                table.getRows().get(r + 1).getCells().get(c).getParagraphs().get(0).setText(data[r][c]);
            }
        }
    }

    /*Since the invoice template already has a row (the second row) for displaying the information of one item,
 we need to determine if it is necessary to add more rows.
If the customer only purchased one item, we’re not required to do anything before inserting the pruchase data.
Otherwise, we need to add rows to accommodate more items and update the formulas dynamically so as to get the correct payment amount.
The writeDataToDocument() method takes a parameter of an String[][] object, which stores one customer’s purchase information.
Each element of it is a String array that can be set up like this: */
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

    /**
     * Generates a PDF invoice for a given order.
     *
     * @param order                the order information
     * @param productLines         the products information
     * @param generatedInvoicePath the path where the invoice should be generated to
     * @return a generated invoice in a PDF format
     */
    public Document generateInvoice(OrderEntity order, List<ProductLine> productLines, String generatedInvoicePath) {
        // load the template file
        Document doc = new Document();
        // TODO Move this path to application.properties
        doc.loadFromFile("src/main/resources/order-templates/Invoice-Template.docx");

        // replace the placeholders from the invoice template
        doc.replace("#InvoiceNum", order.getId(), true, true);
        doc.replace("#CompanyName", order.getUser().getLastName() + " " + order.getUser().getFirstName(), true, true);
        doc.replace("#CompanyAddress", order.getUser().getAddressEntity().getAddress(), true, true);
        doc.replace("#CityStateZip", order.getUser().getAddressEntity().getCity(), true, true);
        doc.replace("#Country", order.getUser().getAddressEntity().getCounty(), true, true);
        doc.replace("#Tel1", order.getUser().getTelephone(), true, true);
        doc.replace("#ContactPerson", order.getUser().getLastName() + " " + order.getUser().getFirstName(), true, true);
        doc.replace("#ShippingAddress", order.getUser().getAddressEntity().getAddress(), true, true);
        doc.replace("#Tel2", order.getUser().getTelephone(), true, true);

        // create table with purchased products
        String[][] purchaseData = new String[productLines.size()][3];
        for (int i = 0; i < productLines.size(); i++) {
            ProductLine productLine = productLines.get(i);
            purchaseData[i] = new String[]{productLine.getDescription(), String.valueOf(productLine.getQuantity()),
                    String.valueOf(productLine.getUnitPrice())};
        }

        // write the purchase data to the document
        writeDataToDocument(doc, purchaseData);

        // update fields
        doc.isUpdateFields(true);

        // save file in pdf format
        doc.saveToFile(generatedInvoicePath, FileFormat.PDF);
        return doc;
    }
}

