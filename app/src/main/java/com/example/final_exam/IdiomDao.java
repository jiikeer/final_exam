package com.example.final_exam;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IdiomDao {
    private IdiomDatabaseHelper dbHelper;

    public IdiomDao(Context context) {
        dbHelper = new IdiomDatabaseHelper(context);
    }

    public List<IdiomModel> getRandomIdiomsByLevel(String level, int count) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<IdiomModel> idioms = new ArrayList<>();

        String[] columns = {IdiomDatabaseHelper.COLUMN_ID,
                IdiomDatabaseHelper.COLUMN_IDIOM,
                IdiomDatabaseHelper.COLUMN_EXPLANATION,
                IdiomDatabaseHelper.COLUMN_LEVEL};
        String selection = IdiomDatabaseHelper.COLUMN_LEVEL + " = ?";
        String[] selectionArgs = {level};

        Cursor cursor = db.query(IdiomDatabaseHelper.TABLE_IDIOMS,
                columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            List<IdiomModel> allIdioms = new ArrayList<>();
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(IdiomDatabaseHelper.COLUMN_ID));
                @SuppressLint("Range") String idiom = cursor.getString(cursor.getColumnIndex(IdiomDatabaseHelper.COLUMN_IDIOM));
                @SuppressLint("Range") String explanation = cursor.getString(cursor.getColumnIndex(IdiomDatabaseHelper.COLUMN_EXPLANATION));
                @SuppressLint("Range") String idiomLevel = cursor.getString(cursor.getColumnIndex(IdiomDatabaseHelper.COLUMN_LEVEL));
                allIdioms.add(new IdiomModel(id, idiom, explanation, idiomLevel));
            } while (cursor.moveToNext());

            // 随机选择指定数量的成语
            Random random = new Random();
            int size = Math.min(count, allIdioms.size());
            while (idioms.size() < size) {
                int index = random.nextInt(allIdioms.size());
                idioms.add(allIdioms.get(index));
                allIdioms.remove(index);
            }
        }
        cursor.close();
        return idioms;
    }
}
