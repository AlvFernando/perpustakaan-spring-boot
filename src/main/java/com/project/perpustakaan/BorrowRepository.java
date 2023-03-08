package com.project.perpustakaan;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import jakarta.transaction.Transactional;

public interface BorrowRepository extends CrudRepository<Borrow,Integer>{
    @Transactional
    @Modifying
    @Query("update Borrow b set b.duration = b.duration+?1 where b.id=?2")
    void setBorrowDurationById(Integer days, Integer id);

    @Query(
        nativeQuery = true,
        value = 
            "SELECT "+
                "borrower_name,"+
                "title as book_title,"+
                "author as book_author,"+
                "CONCAT(duration,' days') as duration,"+
                "borrow_date,"+
                "date_add(borrow_date,INTERVAL duration DAY) as expected_return_date "+
            "FROM borrow b JOIN book bk ON b.book_id = bk.id"
    )
    List<Object[]> generateReport();

    @Query(
        nativeQuery = true,
        value = 
            "SELECT "+
                "title as book_title,"+
                "author as book_author,"+
                "count(title) as total "+
            "FROM borrow b JOIN book bk ON b.book_id = bk.id "+
            "group by title,author"
    )
    List<Object[]> generateReport2();
}
