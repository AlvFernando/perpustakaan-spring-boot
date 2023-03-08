package com.project.perpustakaan;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.source.ByteArrayOutputStream;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;



@Controller
public class MainController {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private ReportRepository reportRepository;

    @GetMapping("/books")
    public @ResponseBody Iterable<Book> books(){
        return bookRepository.findAll();
    }

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @PostMapping("/submit")
    public ResponseEntity<Object> submitBorrowingData(
        @RequestParam String fname,
        @RequestParam String lname,
        @RequestParam Integer bookId,
        @RequestParam Integer duration){
        
        //validation
        if(fname.isEmpty()){
            return ResponseHandler.generateResponse(
                "Please input your first name", 
                HttpStatus.MULTI_STATUS, 
                null
            );
        }

        //getting current date
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd");
        LocalDate localDate = LocalDate.now();

        //saving data
        try {
            Borrow borrow = new Borrow();
            borrow.setBorrowerName(fname+" "+lname);
            borrow.setBookId(bookId);
            borrow.setDuration(duration);
            borrow.setBorrowDate(dtf.format(localDate));

            borrowRepository.save(borrow);

            return ResponseHandler.generateResponse(
                "Saved!", 
                HttpStatus.OK, 
                borrow
            );
        } catch (Exception e) {
            //error handling
            return ResponseHandler.generateResponse(
                e.getMessage(), 
                HttpStatus.MULTI_STATUS, 
                null
            );
        }
    }

    @GetMapping("/admin")
    public String admin(){
        return "admin";
    }

    @GetMapping("/borrowlist")
    public @ResponseBody Iterable<Borrow> borrowList(){
        return borrowRepository.findAll();
    }

    @GetMapping("/extend")
    public String extend(){
        return "update";
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateData(@RequestParam Integer id,@RequestParam Integer days){
        try {
            borrowRepository.setBorrowDurationById(days, id);
        } catch (Exception e) {
            return ResponseHandler.generateResponse(
                e.getMessage(), 
                HttpStatus.MULTI_STATUS, 
                null
            );
        }
        return ResponseHandler.generateResponse(
            "Updated!", 
            HttpStatus.OK, 
            null
        );
    }

    @GetMapping("/cancel")
    public String cancel(){
        return "cancel";
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> delete(@RequestParam Integer id){
        try {
            borrowRepository.deleteById(id);
        } catch (Exception e) {
            return ResponseHandler.generateResponse(
                e.getMessage(), 
                HttpStatus.MULTI_STATUS, 
                null
            );
        }
        return ResponseHandler.generateResponse(
            "Deleted!", 
            HttpStatus.OK, 
            null
        );
    }

    @GetMapping("/report")
    public ResponseEntity<Object> report(){
        return ResponseHandler.generateResponse(
            "This is Report", 
            HttpStatus.OK, 
            borrowRepository.generateReport()
        );
    }

    @GetMapping("/report2")
    public ResponseEntity<Object> report2(){
        return ResponseHandler.generateResponse(
            "This is Report 2", 
            HttpStatus.OK, 
            borrowRepository.generateReport2()
        );
    }

    @GetMapping("/customreport")
    public ResponseEntity<Object> customReport(){
        return ResponseHandler.generateResponse(
            "This is Custom Report", 
            HttpStatus.OK, 
            reportRepository.generateReport()
        );
    }
    
    @GetMapping("/downloadpdf")
    public ResponseEntity<byte[]> download() throws IOException{
        HtmlConverter.convertToPdf(new File("src/main/resources/templates/htmltopdf.html"),new File("src/main/resources/templates/demo-html.pdf"));
        byte[] contents = Files.readAllBytes(Paths.get("src/main/resources/templates/demo-html.pdf"));
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "output.pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        
        Path fileToDeletePath = Paths.get("src/main/resources/templates/demo-html.pdf");
        Files.delete(fileToDeletePath);

        return response;
    }

    @Autowired
    ServletContext servletContext;
    private final TemplateEngine templateEngine;

    public MainController(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }
    @GetMapping("/downloadpdf2")
    public ResponseEntity<?> download2(HttpServletRequest request, HttpServletResponse response) throws IOException{
        Context context = new Context();
        String orderHtml = templateEngine.process("htmltopdf", context);

        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri("http://localhost:8080");
        /* Call convert method */
        HtmlConverter.convertToPdf(orderHtml, target, converterProperties);
        byte[] bytes = target.toByteArray();

        return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=order.pdf")
        .contentType(MediaType.APPLICATION_PDF)
        .body(bytes);
    }
}
