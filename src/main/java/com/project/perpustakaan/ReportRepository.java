package com.project.perpustakaan;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ReportRepository extends CrudRepository<Report,String>{
    @Query(
        nativeQuery = true,
        value = 
            "SELECT "+
                "bk.id,"+
                "borrower_name,"+
                "title as book_title,"+
                "author as book_author,"+
                "CONCAT(duration,' days') as duration,"+
                "borrow_date,"+
                "date_add(borrow_date,INTERVAL duration DAY) as expected_return_date "+
            "FROM borrow b JOIN book bk ON b.book_id = bk.id"
    )
    List<Report> generateReport();
}
