package com.example.final_exam;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        IdiomDatabaseHelper dbHelper = new IdiomDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 确保包含 _id 列
        String[] columns = {
                IdiomDatabaseHelper.COLUMN_ID + " AS _id", // 添加 _id 别名
                IdiomDatabaseHelper.COLUMN_GUESSED_IDIOM,
                IdiomDatabaseHelper.COLUMN_GUESSED_LEVEL,
                IdiomDatabaseHelper.COLUMN_TIMESTAMP
        };

        Cursor cursor = db.query(
                IdiomDatabaseHelper.TABLE_GUESSED_IDIOMS,
                columns,
                null, null, null, null,
                IdiomDatabaseHelper.COLUMN_TIMESTAMP + " DESC"
        );

        // 设置适配器
        String[] from = {
                IdiomDatabaseHelper.COLUMN_GUESSED_IDIOM,
                IdiomDatabaseHelper.COLUMN_GUESSED_LEVEL,
                IdiomDatabaseHelper.COLUMN_TIMESTAMP
        };

        int[] to = {R.id.idiom_text, R.id.level_text, R.id.date_text};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.record_item,
                cursor,
                from,
                to,
                0
        );

        ListView listView = findViewById(R.id.record_list);
        listView.setAdapter(adapter);

        // 显示总数
        TextView countText = findViewById(R.id.total_count);
        countText.setText("已猜对成语总数: " + cursor.getCount());

        // 初始化返回按钮
        Button backButton = findViewById(R.id.back_to_difficulty_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动选择难度界面
                Intent intent = new Intent(RecordActivity.this, DifficultySelectionActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}