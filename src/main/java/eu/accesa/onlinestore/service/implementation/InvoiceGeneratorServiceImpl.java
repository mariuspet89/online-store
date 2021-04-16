package eu.accesa.onlinestore.service.implementation;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import eu.accesa.onlinestore.exceptionhandler.OnlineStoreException;
import eu.accesa.onlinestore.model.entity.OrderEntity;
import eu.accesa.onlinestore.model.invoice.ProductLine;
import eu.accesa.onlinestore.service.InvoiceGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class InvoiceGeneratorServiceImpl implements InvoiceGeneratorService {
    public InvoiceGeneratorServiceImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private MessageSource messageSource;
    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceGeneratorServiceImpl.class);

    @Override
    public ByteArrayOutputStream createPDF(OrderEntity order, List<ProductLine> productLines) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document();

            Locale locale = LocaleContextHolder.getLocale();

            PdfWriter.getInstance(document, outputStream);

            //Inserting image in PDF
            byte[] imageAsBytes = InvoiceGeneratorServiceImpl.class.getResourceAsStream("/logo.jpg").readAllBytes();

            //Setting header image
            Image image = Image.getInstance(imageAsBytes);
            //Setting image width, height
            image.scaleAbsolute(540f, 72f);

            PdfPTable irdTable = new PdfPTable(2);
            String invoiceNumber = messageSource.getMessage("invoice.number", null, locale);
            irdTable.addCell(getIRDCell(invoiceNumber));
            String invoiceDate = messageSource.getMessage("invoice.date", null, locale);
            irdTable.addCell(getIRDCell(invoiceDate));
            //Passing invoice no
            irdTable.addCell(getIRDCell(order.getId()));
            //Passing invoice date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime orderDate = order.getOrderDate();
            irdTable.addCell(getIRDCell(orderDate.format(formatter)));

            PdfPTable irhTable = new PdfPTable(2);
            irhTable.setWidthPercentage(100);

            irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
            irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
            String invoice = messageSource.getMessage("invoice", null, locale);
            irhTable.addCell(getIRHCell(invoice, PdfPCell.ALIGN_RIGHT));
            irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
            irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));

            PdfPCell invoiceTable = new PdfPCell(irdTable);
            invoiceTable.setBorder(0);

            irhTable.addCell(invoiceTable);

            FontSelector fs = new FontSelector();
            Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Font.BOLD);
            fs.addFont(font);

            //Defining font for name and address
            Font nameAndAddressFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 12, Font.BOLDITALIC);

            //Customer information
            String billTo = messageSource.getMessage("bill.to", null, locale);
            Phrase bill = fs.process(billTo);

            //Customer name
            Paragraph name = new Paragraph(order.getUser().getFirstName() + " " + order.getUser().getLastName());
            name.setIndentationLeft(20);
            name.setFont(nameAndAddressFont);

            //Customer telephone
            Paragraph contact = new Paragraph(order.getUser().getTelephone());
            contact.setIndentationLeft(20);

            //Customer address
            Paragraph address = new Paragraph(order.getUser().getAddressEntity().getAddress() + ","
                    + order.getUser().getAddressEntity().getCity() + "," + order.getUser().getAddressEntity().getCounty());
            address.setIndentationLeft(20);
            address.setFont(nameAndAddressFont);

            //One page contains 7 empty additional records
            PdfPTable billTable = new PdfPTable(5);
            billTable.setWidthPercentage(100);
            billTable.setWidths(new float[]{2, 4, 1, 1, 1});
            billTable.setSpacingBefore(30.0f);
            String itemId = messageSource.getMessage("item.id", null, locale);
            billTable.addCell(getBillHeaderCell(itemId));
            String description = messageSource.getMessage("description", null, locale);
            billTable.addCell(getBillHeaderCell(description));
            String unitPrice = messageSource.getMessage("unit.price", null, locale);
            billTable.addCell(getBillHeaderCell(unitPrice));
            String unitQuantity = messageSource.getMessage("quantity", null, locale);
            billTable.addCell(getBillHeaderCell(unitQuantity));
            String amount = messageSource.getMessage("amount", null, locale);
            billTable.addCell(getBillHeaderCell(amount));

            for (ProductLine productLine : productLines) {

                Double price = productLine.getUnitPrice();
                Integer quantity = productLine.getQuantity();

                billTable.addCell(getBillRowCell(productLine.getId()));
                billTable.addCell(getBillRowCell(productLine.getDescription()));
                billTable.addCell(getBillRowCell(price.toString()));
                billTable.addCell(getBillRowCell(quantity.toString()));
                billTable.addCell(getBillRowCell((Double.toString(price * quantity))));
            }

            PdfPTable validity = new PdfPTable(1);
            validity.setWidthPercentage(100);
            String warranty = messageSource.getMessage("warranty", null, locale);
            validity.addCell(getValidityCell(warranty));
            String warrantyLine1 = messageSource.getMessage("warranty.line1", null, locale);
            validity.addCell(getValidityCell(warrantyLine1));
            String warrantyLine2 = messageSource.getMessage("warranty.line2", null, locale);
            validity.addCell(getValidityCell(warrantyLine2));

            PdfPCell summaryL = new PdfPCell(validity);
            summaryL.setColspan(2);
            summaryL.setPadding(1.0f);
            billTable.addCell(summaryL);

            PdfPTable accounts = new PdfPTable(3);
            accounts.setWidthPercentage(100);
            String subtotal = messageSource.getMessage("subtotal", null, locale);
            accounts.addCell(getAccountsCell(subtotal));
            String vat = messageSource.getMessage("vat", null, locale);
            accounts.addCell(getAccountsCell(vat));
            String total = messageSource.getMessage("total", null, locale);
            accounts.addCell(getAccountsCell(total));

            DecimalFormat df = new DecimalFormat("#.##");
            double invoiceSubTotalValue = 0;
            double taxTotalValue = 0;

            for (ProductLine productLine : productLines) {

                Double price = productLine.getUnitPrice();
                Integer quantity = productLine.getQuantity();

                double productTotalValue = (price * quantity);
                invoiceSubTotalValue += (productTotalValue * 0.84033);
                taxTotalValue += (productTotalValue * 0.15967);
            }

            //Adding total cells
            accounts.addCell(getBillRowCell(df.format(invoiceSubTotalValue)));
            accounts.addCell(getBillRowCell(df.format(taxTotalValue)));
            accounts.addCell(getBillRowCell(df.format(invoiceSubTotalValue + taxTotalValue)));

            PdfPCell summaryR = new PdfPCell(accounts);
            summaryR.setColspan(3);
            billTable.addCell(summaryR);

            //Opening PDF document
            document.open();

            //Adding contents table for invoice...
            document.add(image);
            document.add(irhTable);
            document.add(bill);
            document.add(name);
            document.add(contact);
            document.add(address);
            document.add(billTable);

            document.close();
            outputStream.close();

            return outputStream;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new OnlineStoreException(e.getMessage());
        }
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

