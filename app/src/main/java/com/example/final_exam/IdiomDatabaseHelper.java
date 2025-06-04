package com.example.final_exam;
import android.content.ContentValues;
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
    // 添加新表记录猜对的成语
    public static final String TABLE_GUESSED_IDIOMS = "guessed_idioms";
    public static final String COLUMN_GUESSED_IDIOM = "guessed_idiom";
    public static final String COLUMN_GUESSED_LEVEL = "level";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String CREATE_TABLE_GUESSED_IDIOMS =
            "CREATE TABLE " + TABLE_GUESSED_IDIOMS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // 这个就是 _id
                    COLUMN_GUESSED_IDIOM + " TEXT NOT NULL, " +
                    COLUMN_GUESSED_LEVEL + " TEXT NOT NULL, " +
                    COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP);";

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
        db.execSQL(CREATE_TABLE_GUESSED_IDIOMS); // 添加新表
        // 初始化成语数据
        initIdiomData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 升级数据库时添加新表
        if (oldVersion < 2) {
            db
                    .execSQL(CREATE_TABLE_GUESSED_IDIOMS);
        }
    }

    private void initIdiomData(SQLiteDatabase db) {
        //小学难度
        insertIdiom(db, "兴高采烈", "形容非常高兴的神情", "primary");
        insertIdiom(db, "怒气冲冲", "形容非常生气的样子", "primary");
        insertIdiom(db, "聚精会神", "形容注意力高度集中", "primary");
        insertIdiom(db,"自言自语","一个人和自己说话","primary");
        insertIdiom(db, "春暖花开", "比喻游览、观赏的大好时机", "primary");
        insertIdiom(db, "恩重如山", "比喻恩情深厚，像山一样深重", "primary");
        insertIdiom(db, "日积月累", "指长时间不断地积累", "primary");
        insertIdiom(db, "瓜熟蒂落", "指时机一旦成熟，事情自然成功", "primary");
        insertIdiom(db, "学无止境", "比喻学业上是没有尽头的，应奋进不息", "primary");
        insertIdiom(db, "笨鸟先飞", "比喻能力差的人怕落后，做事比别人先动手", "primary");
        //中学难度
        insertIdiom(db, "爱憎分明", "对人或事物喜爱和憎恨的界限非常清楚", "middle");
        insertIdiom(db, "安然无恙", "平安无事，没有受到损害", "middle");
        insertIdiom(db, "跋山涉水", "形容走长路的辛苦", "middle");
        insertIdiom(db, "百看不厌", "形容文章或书籍写得非常好，再看多少遍也不厌倦", "middle");
        insertIdiom(db, "搬弄是非", "在别人背后传闲话、乱加议论，引起纠纷", "middle");
        insertIdiom(db, "变本加厉", "原指比原来的更加发展，现在指比原来变得更加严重", "middle");
        insertIdiom(db, "变幻莫测", "变化又多又快，使人无法捉摸", "middle");
        insertIdiom(db, "别具匠心", "另有一种巧妙的心思，指在文学和技艺方面有与众不同的巧妙构思", "middle");
        insertIdiom(db, "不出所料", "没有超出意料之外，原来就预料到的", "middle");
        //高中难度
        insertIdiom(db, "耿耿于怀", "心事萦绕，不能忘怀", "senior");
        insertIdiom(db, "罪不容诛", "指杀了也抵不了其所犯的罪行。形容罪大恶极", "senior");
        insertIdiom(db, "屡试不爽", "屡次试验，都没有差错", "senior");
        insertIdiom(db, "大快人心", "指坏人坏事受到惩罚，使人们心里感到非常痛快", "senior");
        insertIdiom(db, "事半功倍", "形容费力小，收效大", "senior");
        insertIdiom(db, "莘莘学子", "众多的学子", "senior");
        insertIdiom(db, "炙手可热", "比喻气炎盛，权势大", "senior");
        insertIdiom(db, "沆瀣一气", "比喻气味相投者结合在一起", "senior");
        insertIdiom(db, "叹为观止", "赞美所看到的事物好到了极点", "senior");

    }

    private void insertIdiom(SQLiteDatabase db, String idiom, String explanation, String level) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_IDIOM, idiom);
        values.put(COLUMN_EXPLANATION, explanation);
        values.put(COLUMN_LEVEL, level);
        db.insert(TABLE_IDIOMS, null, values);
    }
    // 添加记录猜对成语的方法
    public void insertGuessedIdiom(SQLiteDatabase db, String idiom, String level) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_GUESSED_IDIOM, idiom);
        values.put(COLUMN_GUESSED_LEVEL, level);
        db.insert(TABLE_GUESSED_IDIOMS, null, values);
    }

}