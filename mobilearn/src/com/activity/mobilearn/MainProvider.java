package com.activity.mobilearn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
 
public class MainProvider {
 
	
	public static final String KEY_PRICE = "price";
	public static final String KEY_OWNER = "owner";
	public static final String KEY_PERSENT = "persent";
	public static final String KEY_THUMB_URL = "thumb_url";
    public static final String KEY_QUESTION = "question";
    public static final String KEY_COUNT = "count";
    public static final String KEY_NAME = "name";
    public static final String KEY_TYPE = "type";
    public static final String KEY_UPDATE_DATE = "update_date";
    public static final String KEY_REG_DATE = "reg_date";
    public static final String KEY_IS_USE = "is_use";
    public static final String KEY_REPLY = "reply";
    public static final String KEY_ANSWER = "answer";
    public static final String KEY_OID = "oid";
    public static final String KEY_OID_LIBRARY = "oid_library";
    public static final String KEY_OID_QUESTION = "oid_question";
    public static final String KEY_OID_PLAYLIST = "oid_playlist";
    public static final String KEY_CORRECT_ANSWER_CNT = "correct_answer_cnt";
    public static final String KEY_STATE = "state"; // user : 0, seller : 1
    public static final String KEY_ITEM = "item";
    public static final String KEY_VALUE = "value";
    public static final String KEY_TITLE = "title";
    public static final String KEY_SCORE = "score";
    public static final String KEY_CORRECT_FLAG = "correct_flag"; // incorrect : 0, correct : 1, skip : 2
    
    
    private static final String TAG = "MainProvider";
 
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
 
    /**
     *
     * Database creation sql statement
     */
    private static final String TABLE_PLAYLIST_CREATE = "create table playlists (oid integer primary key not null, "
            + "title text not null, oid_library integer not null );";
    private static final String TABLE_PLAYLIST_QUESTION_CREATE = "create table playlists_questions (oid_playlist integer not null, "
            + "oid_question integer not null );";
    private static final String TABLE_ANSWER_CREATE = "create table answers (oid integer primary key not null, "
            + "reply text not null, answer integer not null, oid_question integer not null );";
    private static final String TABLE_QUESTION_CREATE = "create table questions (oid integer primary key not null, "
            + "question text not null, oid_library integer not null, count integer default 0, "
    		+ "correct_answer_cnt integer default 0, state integer default 0, score integer default 0);";
    private static final String TABLE_LIBRARY_CREATE = "create table library (oid integer primary key not null, "
    		+ "name text not null, type integer not null, update_date text not null, "
    		+ "is_use integer not null);";
    private static final String TABLE_SETTING_CREATE = "create table setting (oid integer primary key autoincrement not null, "
            + "item text not null, value text not null );";
    private static final String TABLE_LOG_CREATE = "create table logs (oid integer primary key autoincrement not null, "
            + "oid_question integer not null, correct_flag integer not null, score integer default 0, "
    		+ "reg_date integer test not null);";
 
    private static final String DATABASE_NAME = "mobilearn";
    private static final String TABLE_QUESTION = "questions";
    private static final String TABLE_PLAYLIST = "playlists";
    private static final String TABLE_PLAYLIST_QUESTION = "playlists_questions";
    private static final String TABLE_LIBRARY = "library";
    private static final String TABLE_ANSWER = "answers";
    private static final String TABLE_SETTING = "setting";
    private static final String TABLE_LOG = "logs";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;
 
    private static class DatabaseHelper extends SQLiteOpenHelper {
 
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
 
        @Override
        public void onCreate(SQLiteDatabase db) {
        	db.execSQL(TABLE_PLAYLIST_CREATE);
        	db.execSQL(TABLE_PLAYLIST_QUESTION_CREATE);
        	db.execSQL(TABLE_ANSWER_CREATE);
            db.execSQL(TABLE_QUESTION_CREATE);
            db.execSQL(TABLE_LIBRARY_CREATE);
            db.execSQL(TABLE_SETTING_CREATE);
            db.execSQL(TABLE_LOG_CREATE);
        }
 
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST_QUESTION);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANSWER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIBRARY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTING);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOG);
            onCreate(db);
        }
        
        public void init(SQLiteDatabase db) {
        	db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST);
        	db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST_QUESTION);
        	db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANSWER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIBRARY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTING);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOG);
            onCreate(db);
        }
    }
 
    public MainProvider(Context ctx) {
        this.mCtx = ctx;
    }
 
    public MainProvider open() throws SQLException {
    	Log.d("MainProvider", "Provider :: MainProvider:open()");
        mDbHelper = new DatabaseHelper(this.mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public MainProvider init() throws SQLException {
    	mDbHelper = new DatabaseHelper(this.mCtx);
    	mDb = mDbHelper.getWritableDatabase();
    	mDbHelper.init(mDb);
    	return this;
    }

    public void close() {
        mDbHelper.close();
    }
 
    public long createQuestion(int oid, String question, int oid_library) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_OID, oid);
        initialValues.put(KEY_QUESTION, question);
        initialValues.put(KEY_OID_LIBRARY, oid_library);
        return mDb.insert(TABLE_QUESTION, null, initialValues);
    }
 
    public boolean deleteQuestion(long rowId) {
        Log.i("Delete called", "value__" + rowId);
        return mDb.delete(TABLE_QUESTION, KEY_OID + "=" + rowId, null) > 0;
    }
 
    public Cursor fetchAllQuestionInLibrary(long oid_library) {
    	String sql = "SELECT " + KEY_OID +", "
    			+ KEY_QUESTION + ", " 
    			+ KEY_COUNT + ", "
    			+ KEY_CORRECT_ANSWER_CNT + ", "
    			+ KEY_STATE + " "
    			+ " FROM " + TABLE_QUESTION
    			+ " WHERE  " + KEY_OID_LIBRARY + " = " + oid_library
    			+ " ORDER BY " + KEY_QUESTION + " ASC";
        Cursor mCursor = mDb.rawQuery(sql, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
        //return mDb.query(TABLE_QUESTION, new String[] { KEY_OID, KEY_QUESTION, KEY_COUNT, KEY_CORRECT_ANSWER_CNT, KEY_STATE }, null, null, null, null, KEY_QUESTION + " ASC");
    }
    
    public Cursor fetchAllQuestionInLibraryForCount(long oid_library) {
    	String sql = "SELECT COUNT(*) "
    			+ " FROM " + TABLE_QUESTION
    			+ " WHERE  " + KEY_OID_LIBRARY + " = " + oid_library;
        Cursor mCursor = mDb.rawQuery(sql, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
        //return mDb.query(TABLE_QUESTION, new String[] { KEY_OID, KEY_QUESTION, KEY_COUNT, KEY_CORRECT_ANSWER_CNT, KEY_STATE }, null, null, null, null, KEY_QUESTION + " ASC");
    }
 
    public Cursor fetchQuestion(long oid_library, int limit) throws SQLException {
    	String sql = "SELECT oid, question " 
    			+ " FROM " + TABLE_QUESTION
    			+ " WHERE "+ KEY_OID_LIBRARY +" = " + oid_library
    			+ " ORDER BY count ASC, oid ASC"
    			+ " LIMIT " + limit;
        Cursor mCursor = mDb.rawQuery(sql, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public Cursor fetchOneQuestion(long oid_question) throws SQLException {
    	String sql = "SELECT oid, question " 
    			+ " FROM " + TABLE_QUESTION
    			+ " WHERE "+ KEY_OID +" = " + oid_question;
        Cursor mCursor = mDb.rawQuery(sql, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public void updateCountQuestion(long oid) {
    	String sql = "UPDATE " + TABLE_QUESTION 
    			+ " SET " + KEY_COUNT + " = " + KEY_COUNT + " + 1 "
    			+ " WHERE " + KEY_OID + " = " + oid;
        mDb.execSQL(sql);
        /*
        ContentValues args = new ContentValues();
        args.put(KEY_COUNT, KEY_COUNT + 1);
        return mDb.update(TABLE_QUESTION, args, KEY_OID + "=" + oid, null) > 0;
        */
    }
    
    public void updateCorrectQuestion(long oid) {
    	String sql = "UPDATE " + TABLE_QUESTION 
    			+ " SET " + KEY_CORRECT_ANSWER_CNT + " = " + KEY_CORRECT_ANSWER_CNT + " + 1 "
    			+ " WHERE " + KEY_OID + " = " + oid;
        mDb.execSQL(sql);
        /*
        ContentValues args = new ContentValues();
        args.put(KEY_CORRECT_ANSWER_CNT, KEY_CORRECT_ANSWER_CNT + 1);
        return mDb.update(TABLE_QUESTION, args, KEY_OID + "=" + oid, null) > 0;
        */
    }
    
    public long insertLibrary(int oid, String name, int type, String update_date, int is_use) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_OID, oid);
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_TYPE, type);
        initialValues.put(KEY_UPDATE_DATE, update_date);
        initialValues.put(KEY_IS_USE, is_use);
        return mDb.insert(TABLE_LIBRARY, null, initialValues);
    }
 
    public boolean deleteLibrary(long oid) {
        Log.i("Delete called", "value__" + oid);
        return mDb.delete(TABLE_LIBRARY, KEY_OID + "=" + oid, null) > 0;
    }
 
    public Cursor fetchAllLibrary() {
        return mDb.query(TABLE_LIBRARY, new String[] { KEY_OID, KEY_NAME, KEY_TYPE, KEY_UPDATE_DATE }, null, null, null, null, KEY_NAME + " ASC");
    }
 
    public Cursor fetchLibrary(long oid) throws SQLException {
 
        Cursor mCursor = mDb.query(true, TABLE_LIBRARY, new String[] { KEY_OID, KEY_NAME, KEY_TYPE, KEY_UPDATE_DATE }, KEY_OID
                + "=" + oid, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public long insertAnswer(int oid, String reply, int answer, int oid_question) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_OID, oid);
        initialValues.put(KEY_REPLY, reply);
        initialValues.put(KEY_ANSWER, answer);
        initialValues.put(KEY_OID_QUESTION, oid_question);
        return mDb.insert(TABLE_ANSWER, null, initialValues);
    }
 
    public boolean deleteAnswer(long oid) {
        Log.i("Delete called", "value__" + oid);
        return mDb.delete(TABLE_ANSWER, KEY_OID + "=" + oid, null) > 0;
    }
 
    public Cursor fetchAllAnswer() {
        return mDb.query(TABLE_ANSWER, new String[] { KEY_OID, KEY_REPLY, KEY_ANSWER, KEY_OID_QUESTION }, null, null, null, null, KEY_NAME + " ASC");
    }
 
    public Cursor fetchTrueAnswer(long oid) throws SQLException {
        Cursor mCursor = mDb.query(true, TABLE_ANSWER, new String[] { KEY_REPLY }, KEY_OID_QUESTION
                + "=" + oid + " AND " + KEY_ANSWER + " = 1" , null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public Cursor fetchFalseAnswer(long oid, int limit) throws SQLException {
    	String sql = "SELECT " + KEY_REPLY 
    			+ " FROM " + TABLE_ANSWER
    			+ " WHERE "+ KEY_OID_QUESTION +" != " + oid
    			+ " LIMIT " + limit;
        Cursor mCursor = mDb.rawQuery(sql, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public long insertSetting(String item, String value) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ITEM, item);
        initialValues.put(KEY_VALUE, value);
        return mDb.insert(TABLE_SETTING, null, initialValues);
    }
 
    public boolean deleteSetting(String item) {
        return mDb.delete(TABLE_SETTING, KEY_ITEM + "= '" + item + "'", null) > 0;
    }
    public Cursor fetchSetting(String item) throws SQLException {
        Cursor mCursor = mDb.query(true, TABLE_SETTING, new String[] { KEY_VALUE }, KEY_ITEM
                + "= '" + item + "'", null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public Cursor fetchAllSetting() throws SQLException {
        Cursor mCursor = mDb.query(true, TABLE_SETTING, new String[] { KEY_ITEM, KEY_VALUE }, null, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public boolean updateSettingItem(String item, String value) {
        ContentValues args = new ContentValues();
        args.put(KEY_VALUE, value);
        return mDb.update(TABLE_SETTING, args, KEY_ITEM + "= '" + item + "'", null) > 0;
    }
    
    public long insertPlayList(String title, long oid_library) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_OID_LIBRARY, oid_library);
        return mDb.insert(TABLE_PLAYLIST, null, initialValues);
    }
    
    public boolean deletePlayList(long oid_playlist) {
        return mDb.delete(TABLE_PLAYLIST, KEY_OID + "=" + oid_playlist, null) > 0;
    }
    
    public Cursor fetchAllPlayList() {
    	return mDb.query(TABLE_PLAYLIST, new String[] { KEY_OID, KEY_TITLE }, null, null, null, null, KEY_OID + " DESC");
    }
    
    public long insertPlayListQustion(long oid_playlist, long oid_question) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_OID_PLAYLIST, oid_playlist);
        initialValues.put(KEY_OID_QUESTION, oid_question);
        return mDb.insert(TABLE_PLAYLIST_QUESTION, null, initialValues);
    }
    
    public boolean deletePlayListQustion(long oid_playlist) {
        return mDb.delete(TABLE_PLAYLIST_QUESTION, KEY_OID_PLAYLIST + "=" + oid_playlist, null) > 0;
    }
    
    public Cursor fetchPlayListQuestion(long oid_playlist) throws SQLException {
    	String sql = "SELECT " + TABLE_QUESTION + "." +  KEY_OID
    			+ " , " + TABLE_QUESTION + "." +  KEY_QUESTION
    			+ " , " + TABLE_QUESTION + "." +  KEY_COUNT
    			+ " , " + TABLE_QUESTION + "." +  KEY_CORRECT_ANSWER_CNT
    			+ " , " + TABLE_QUESTION + "." +  KEY_STATE
    			+ " FROM " + TABLE_PLAYLIST_QUESTION
    			+ " INNER JOIN " + TABLE_QUESTION
    			+ " ON " + TABLE_PLAYLIST_QUESTION + "." + KEY_OID_QUESTION + " = " + TABLE_QUESTION + "." + KEY_OID
    			+ " WHERE "+ KEY_OID_PLAYLIST +" = " + oid_playlist;
    			
        Cursor mCursor = mDb.rawQuery(sql, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public long insertLog(long oid_question, int correct_flag, int score, String reg_date) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_OID_QUESTION, oid_question);
        initialValues.put(KEY_CORRECT_FLAG, correct_flag);
        initialValues.put(KEY_SCORE, score);
        initialValues.put(KEY_REG_DATE, reg_date);
        return mDb.insert(TABLE_PLAYLIST_QUESTION, null, initialValues);
    }
}