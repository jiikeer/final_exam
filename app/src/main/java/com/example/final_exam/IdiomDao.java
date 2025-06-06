package com.example.final_exam;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class IdiomDao {
    private IdiomDatabaseHelper dbHelper;

    public IdiomDao(Context context) {
        dbHelper = new IdiomDatabaseHelper(context);
    }

    // 获取已猜对的成语
    public List<String> getGuessedIdioms() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> guessedIdioms = new ArrayList<>();

        String[] columns = {IdiomDatabaseHelper.COLUMN_GUESSED_IDIOM};
        Cursor cursor = db.query(IdiomDatabaseHelper.TABLE_GUESSED_IDIOMS,
                columns
                , null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String idiom = cursor.getString(cursor.getColumnIndex(IdiomDatabaseHelper.COLUMN_GUESSED_IDIOM));
                guessedIdioms.add(idiom);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return guessedIdioms;
    }

    // 记录猜对的成语
    public void recordGuessedIdiom(String idiom, String level) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.insertGuessedIdiom(db, idiom, level);
    }
    //根据难度级别随机获取指定数量的未猜对的成语
    public List<IdiomModel> getRandomIdiomsByLevel(String level, int count) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<IdiomModel> idioms = new ArrayList<>();

        String[] columns = {IdiomDatabaseHelper.COLUMN_ID,
                IdiomDatabaseHelper.COLUMN_IDIOM,
                IdiomDatabaseHelper.COLUMN_EXPLANATION,
                IdiomDatabaseHelper.COLUMN_LEVEL,
                IdiomDatabaseHelper.COLUMN_INTERFERING_PARTS};
        String selection = IdiomDatabaseHelper.COLUMN_LEVEL + " = ?";
        String[] selectionArgs = {level};
        String exclusionQuery = IdiomDatabaseHelper.COLUMN_IDIOM + " NOT IN (" +
                "SELECT " + IdiomDatabaseHelper.COLUMN_GUESSED_IDIOM +
                " FROM " + IdiomDatabaseHelper.TABLE_GUESSED_IDIOMS + ")";

        String limit = String.valueOf(count);

        Cursor cursor = db.query(IdiomDatabaseHelper.TABLE_IDIOMS,
                columns,
                selection + " AND " + exclusionQuery,
                selectionArgs,
                null, null,
                "RANDOM()", // 随机排序
                limit);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(IdiomDatabaseHelper.COLUMN_ID));
                @SuppressLint("Range") String idiom = cursor.getString(cursor.getColumnIndex(IdiomDatabaseHelper.COLUMN_IDIOM));
                @SuppressLint("Range") String explanation = cursor.getString(cursor.getColumnIndex(IdiomDatabaseHelper.COLUMN_EXPLANATION));
                @SuppressLint("Range") String idiomLevel = cursor.getString(cursor.getColumnIndex(IdiomDatabaseHelper.COLUMN_LEVEL));
                @SuppressLint("Range") String interferingParts = cursor.getString(cursor.getColumnIndex(IdiomDatabaseHelper.COLUMN_INTERFERING_PARTS));
                idioms.add(new IdiomModel(id, idiom, explanation, idiomLevel, interferingParts));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return idioms;
    }
}