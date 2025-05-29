package com.example.final_exam;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class IdiomDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "idiom_db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_IDIOMS = "idioms";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_IDIOM = "idiom";
    public static final String COLUMN_EXPLANATION = "explanation";
    public static final String COLUMN_LEVEL = "level";

    private static final String CREATE_TABLE_IDIOMS =
            "CREATE TABLE " + TABLE_IDIOMS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_IDIOM + " TEXT NOT NULL, " +
                    COLUMN_EXPLANATION + " TEXT, " +
                    COLUMN_LEVEL + " TEXT NOT NULL);";

    public IdiomDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_IDIOMS);
        // 初始化成语数据
        initIdiomData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IDIOMS);
        onCreate(db);
    }

    private void initIdiomData(SQLiteDatabase db) {
        // 示例数据：小学难度
        insertIdiom(db, "明察秋毫", "形容眼力好到可以看清极其细小的事物", "primary");
        insertIdiom(db, "杯弓蛇影", "比喻因疑神疑鬼而引起恐惧", "primary");
        // 更多成语数据...
    }

    private void insertIdiom(SQLiteDatabase db, String idiom, String explanation, String level) {
        String sql = "INSERT INTO " + TABLE_IDIOMS + " (" +
                COLUMN_IDIOM + ", " + COLUMN_EXPLANATION + ", " + COLUMN_LEVEL + ") VALUES ('" +
                idiom + "', '" + explanation + "', '" + level + "');";
        db.execSQL(sql);
    }
}
