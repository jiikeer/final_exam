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
    public static final String COLUMN_INTERFERING_PARTS = "interfering_parts"; // 新增字段

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
                    COLUMN_LEVEL + " TEXT NOT NULL, " +
                    COLUMN_INTERFERING_PARTS + " TEXT);"; // 新增字段

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
            db.execSQL(CREATE_TABLE_GUESSED_IDIOMS);
        }
    }

    private void initIdiomData(SQLiteDatabase db) {
        //小学难度
        insertIdiom(db, "兴高采烈", "形容非常高兴的神情", "primary", "膏、彩、踩、列、裂");
        insertIdiom(db, "怒气冲冲", "形容非常生气的样子", "primary", "努、恕、充、重");
        insertIdiom(db, "聚精会神", "形容注意力高度集中", "primary", "钜、经、睛、汇、伸、深");
        insertIdiom(db, "自言自语", "一个人和自己说话", "primary", "字、五、说、宇、烟、艳");
        insertIdiom(db, "春暖花开", "比喻游览、观赏的大好时机", "primary", "日、阳、百、花、齐、放");
        insertIdiom(db, "恩重如山", "比喻恩情深厚，像山一样深重", "primary", "思、茹、删、水、嗯");
        insertIdiom(db, "日积月累", "指长时间不断地积累", "primary", "税、只、越、绩、迹");
        insertIdiom(db, "瓜熟蒂落", "指时机一旦成熟，事情自然成功", "primary", "果、缔、谛、草");
        insertIdiom(db, "学无止境", "比喻学业上是没有尽头的，应奋进不息", "primary", "竞、竟、日、只、立");
        insertIdiom(db, "笨鸟先飞", "比喻能力差的人怕落后，做事比别人先动手", "primary", "苯、夯、竹、本");

        //中学难度
        insertIdiom(db, "爱憎分明", "对人或事物喜爱和憎恨的界限非常清楚", "middle", "受、喜、曾、增、赠");
        insertIdiom(db, "安然无恙", "平安无事，没有受到损害", "middle", "静、羊、勿、烊、佯");
        insertIdiom(db, "跋山涉水", "形容走长路的辛苦", "middle", "拔、拨、足、陟、徒、水、山");
        insertIdiom(db, "百看不厌", "形容文章或书籍写得非常好，再看多少遍也不厌倦", "middle", "恹、艳、砚、观、庆");
        insertIdiom(db, "搬弄是非", "在别人背后传闲话、乱加议论，引起纠纷", "middle", "般、颁、对、错、笼、手");
        insertIdiom(db, "变本加厉", "原指比原来的更加发展，现在指比原来变得更加严重", "middle", "利、励、历、严、木");
        insertIdiom(db, "变幻莫测", "变化又多又快，使人无法捉摸", "middle", "幼、幻、弯、漠、侧、恻");
        insertIdiom(db, "别具匠心", "另有一种巧妙的心思，指在文学和技艺方面有与众不同的巧妙构思", "middle", "俱、各、恪、新、木");
        insertIdiom(db, "不出所料", "没有超出意料之外，原来就预料到的", "middle", "锁、索、户、斤、科、抖、辽");

        //高中难度
        insertIdiom(db, "耿耿于怀", "心事萦绕，不能忘怀", "senior", "梗、哽、耳、予、坏");
        insertIdiom(db, "罪不容诛", "指杀了也抵不了其所犯的罪行。形容罪大恶极", "senior", "非、无、荣、溶、珠、杀");
        insertIdiom(db, "屡试不爽", "屡次试验，都没有差错", "senior", "战、越、站、悲、试、验、败");
        insertIdiom(db, "大快人心", "指坏人坏事受到惩罚，使人们心里感到非常痛快", "senior", "巨、筷、块、人、皆");
        insertIdiom(db, "事半功倍", "形容费力小，收效大", "senior", "试、断、奖、焙、一、举、两、得");
        insertIdiom(db, "莘莘学子", "众多的学子", "senior", "辛、新、锌、申、多、草");
        insertIdiom(db, "炙手可热", "比喻气炎盛，权势大", "senior", "火、病、弱、风、靡、一、时");
        insertIdiom(db, "沆瀣一气", "比喻气味相投者结合在一起", "senior", "沆、泄、亢、抗、蟹、狼、狈、为、奸");
        insertIdiom(db, "叹为观止", "赞美所看到的事物好到了极点", "senior", "只、劝、看、停、登、峰、造、极");
    }

    private void insertIdiom(SQLiteDatabase db, String idiom, String explanation, String level, String interferingParts) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_IDIOM, idiom);
        values.put(COLUMN_EXPLANATION, explanation);
        values.put(COLUMN_LEVEL, level);
        values.put(COLUMN_INTERFERING_PARTS, interferingParts); // 新增字段
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