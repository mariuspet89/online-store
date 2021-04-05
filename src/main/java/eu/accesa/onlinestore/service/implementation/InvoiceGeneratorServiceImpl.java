package eu.accesa.onlinestore.service.implementation;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import eu.accesa.onlinestore.exceptionhandler.EntityNotFoundException;
import eu.accesa.onlinestore.model.entity.OrderEntity;
import eu.accesa.onlinestore.model.entity.ProductEntity;
import eu.accesa.onlinestore.repository.ProductRepository;
import eu.accesa.onlinestore.service.InvoiceGeneratorService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class InvoiceGeneratorServiceImpl implements InvoiceGeneratorService {

    private final ProductRepository productRepository;

    public InvoiceGeneratorServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ByteArrayOutputStream createPDF(OrderEntity order) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);

            //Inserting Image in PDF
            Image image = Image.getInstance("classpath:/src/main/resources/logo.jpg");//Header Image
            image.scaleAbsolute(540f, 72f);//image width,height

            PdfPTable irdTable = new PdfPTable(2);
            irdTable.addCell(getIRDCell("Invoice No"));
            irdTable.addCell(getIRDCell("Invoice Date"));
            irdTable.addCell(getIRDCell(order.getId())); // pass invoice number
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime orderDate = order.getOrderDate();
            irdTable.addCell(getIRDCell(orderDate.format(formatter).toString())); // pass invoice date
            PdfPTable irhTable = new PdfPTable(2);
            irhTable.setWidthPercentage(100);

            irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
            irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
            irhTable.addCell(getIRHCell("Invoice", PdfPCell.ALIGN_RIGHT));
            irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
            irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
            PdfPCell invoiceTable = new PdfPCell(irdTable);
            invoiceTable.setBorder(0);
            irhTable.addCell(invoiceTable);

            FontSelector fs = new FontSelector();
            Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Font.BOLD);
            fs.addFont(font);
            Phrase bill = fs.process("Bill To"); // customer information
            //customer name
            Paragraph name = new Paragraph(order.getUser().getFirstName() + " " + order.getUser().getLastName());
            name.setIndentationLeft(20);
            Font nameAndAdressFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 12, Font.BOLDITALIC);
            name.setFont(nameAndAdressFont);
            Paragraph contact = new Paragraph(order.getUser().getTelephone());
            contact.setIndentationLeft(20);
            //set adress
            Paragraph address = new Paragraph(order.getUser().getAddressEntity().getAddress() + ","
                    + order.getUser().getAddressEntity().getCity() + "," + order.getUser().getAddressEntity().getCounty());
            address.setIndentationLeft(20);
            address.setFont(nameAndAdressFont);

            PdfPTable billTable = new PdfPTable(5); //one page contains 7 empty aditional records
            billTable.setWidthPercentage(100);
            billTable.setWidths(new float[]{2, 4, 1, 1, 1});
            billTable.setSpacingBefore(30.0f);
            billTable.addCell(getBillHeaderCell("Item Id"));
            billTable.addCell(getBillHeaderCell("Description"));
            billTable.addCell(getBillHeaderCell("Unit Price"));
            billTable.addCell(getBillHeaderCell("Qty"));
            billTable.addCell(getBillHeaderCell("Amount"));

            for (String id : order.getOrderedProducts().keySet()) {

                ProductEntity product = productRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(ProductEntity.class.getName(),
                                "Product id", order.getOrderedProducts().keySet().toString()));
                Double price = product.getPrice().doubleValue();
                Integer quantity = order.getOrderedProducts().get(id).intValue();

                billTable.addCell(getBillRowCell(id));
                billTable.addCell(getBillRowCell(product.getDescription()));
                billTable.addCell(getBillRowCell(product.getPrice().toString()));
                billTable.addCell(getBillRowCell(quantity.toString()));
                billTable.addCell(getBillRowCell((Double.toString(price * quantity))));
            }


            PdfPTable validity = new PdfPTable(1);
            validity.setWidthPercentage(100);
            validity.addCell(getValidityCell("Warranty"));
            validity.addCell(getValidityCell(" * Products purchased comes with 2 year national warranty \n   (if applicable)"));
            validity.addCell(getValidityCell(" * Warranty should be claimed only for bikes within manufacturer condition"));

            PdfPCell summaryL = new PdfPCell(validity);
            summaryL.setColspan(2);
            summaryL.setPadding(1.0f);
            billTable.addCell(summaryL);

            PdfPTable accounts = new PdfPTable(3);
            accounts.setWidthPercentage(100);
            accounts.addCell(getAccountsCell("Subtotal VAT excluded"));
            accounts.addCell(getAccountsCell("VAT(19%)"));
            accounts.addCell(getAccountsCell("Total"));

            DecimalFormat df = new DecimalFormat("#.##");
            double invoiceSubTotalValue = 0;
            double taxTotalValue = 0;
            for (String id : order.getOrderedProducts().keySet()) {
                ProductEntity product = productRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(ProductEntity.class.getName(),
                                "Product id", order.getOrderedProducts().keySet().toString()));
                Double price = product.getPrice();
                Integer quantity = order.getOrderedProducts().get(id).intValue();

                double productTotalValue = (price * quantity);
                invoiceSubTotalValue += (productTotalValue * 0.84033);
                taxTotalValue += (productTotalValue * 0.15967);
            }

            // add total cells
            accounts.addCell(getBillRowCell(df.format(invoiceSubTotalValue)));
            accounts.addCell(getBillRowCell(df.format(taxTotalValue)));
            accounts.addCell(getBillRowCell(df.format(invoiceSubTotalValue + taxTotalValue)));

            PdfPCell summaryR = new PdfPCell(accounts);
            summaryR.setColspan(3);
            billTable.addCell(summaryR);


            document.open();//PDF document opened........

            //add contents table for invoice...
            document.add(image);
            document.add(irhTable);
            document.add(bill);
            document.add(name);
            document.add(contact);
            document.add(address);
            document.add(billTable);

            document.close();
            baos.close();

            return baos;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private PdfPCell getIRHCell(String text, int alignment) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 16);
        font.setColor(BaseColor.GRAY);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    private PdfPCell getIRDCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 9);
        font.setColor(BaseColor.GRAY);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5.0f);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        return cell;
    }

    private PdfPCell getBillHeaderCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
        font.setColor(BaseColor.BLUE);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5.0f);
        return cell;
    }

    private PdfPCell getBillRowCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.COURIER_OBLIQUE, 9);
        font.setColor(BaseColor.DARK_GRAY);
        fs.addFont(font);
        fs.process(text);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5.0f);
        cell.setBorderWidthBottom(0);
        cell.setBorderWidthTop(0);
        return cell;
    }

    private PdfPCell getValidityCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 8);
        font.setColor(BaseColor.GRAY);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorder(0);
        return cell;
    }

    private PdfPCell getAccountsCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 8);
        font.setColor(BaseColor.RED);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthTop(0);
        cell.setPadding(5.0f);
        return cell;
    }
}

