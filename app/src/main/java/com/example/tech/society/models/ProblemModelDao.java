package com.example.tech.society.models;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by tech on 9/30/17.
 */
@Dao
public interface ProblemModelDao {

    @Query("select * from ProblemModel")
    List<ProblemModel> getAllBorrowedItems();

    @Query("select * from ProblemModel where id = :id")
    ProblemModel getItembyId(String id);

    @Insert
    void addBorrow(ProblemModel borrowModel);

    @Delete
    void deleteBorrow(ProblemModel borrowModel);
}
