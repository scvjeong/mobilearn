package com.example.mobilearn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
 
public class MainProvider {
 
	public static final String KEY_OID_LIBRARY = "oid_library";
    public static final String KEY_QUESTION = "question";
    public static final String KEY_OID = "oid";
    public static final String KEY_NAME = "name";
    public static final String KEY_TYPE = "type";
    public static final String KEY_UPDATE_DATE = "update_date";
    public static final String KEY_IS_USE = "is_use";
    private static final String TAG = "MainProvider";
 
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
 
    /**
     *
     * Database creation sql statement
     */
 
    private static final String TABLE_QUESTION_CREATE = "create table questions (oid integer primary key, "
            + "question text not null, oid_library interger not null);";
    private static final String TABLE_LIBRARY_CREATE = "create table library (oid integer primary key, "
    		+ "name text not null, type integer not null, update_date text not null, "
    		+ "is_use integer not null);";
 
    private static final String DATABASE_NAME = "mobilearn";
    private static final String TABLE_QUESTION = "questions";
    private static final String TABLE_LIBRARY = "library";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;
 
    private static class DatabaseHelper extends SQLiteOpenHelper {
 
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
 
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE_QUESTION_CREATE);
            db.execSQL(TABLE_LIBRARY_CREATE);
        }
 
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS library");
            db.execSQL("DROP TABLE IF EXISTS questions");
            onCreate(db);
        }
        
        public void init(SQLiteDatabase db) {
        	db.execSQL("DROP TABLE IF EXISTS library");
            db.execSQL("DROP TABLE IF EXISTS questions");
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
 
    public long createQuestion(String question, int oid_library) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_QUESTION, question);
        initialValues.put(KEY_OID_LIBRARY, oid_library);
        return mDb.insert(TABLE_QUESTION, null, initialValues);
    }
 
    public boolean deleteQuestion(long rowId) {
        Log.i("Delete called", "value__" + rowId);
        return mDb.delete(TABLE_QUESTION, KEY_OID + "=" + rowId, null) > 0;
    }
 
    public Cursor fetchAllQuestion() {
        return mDb.query(TABLE_QUESTION, new String[] { KEY_OID, KEY_QUESTION }, null, null, null, null, KEY_QUESTION + " ASC");
    }
 
    public Cursor fetchQuestion(long oid) throws SQLException {
 
        Cursor mCursor = mDb.query(true, TABLE_QUESTION, new String[] { KEY_OID, KEY_QUESTION }, KEY_OID
                + "=" + oid, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
 
    public boolean updateNote(long rowId, String question) {
        ContentValues args = new ContentValues();
        args.put(KEY_QUESTION, question);
        return mDb.update(TABLE_QUESTION, args, KEY_OID + "=" + rowId, null) > 0;
    }
    
    public long insertLibrary(String name, int type, String update_date, int is_use) {
        ContentValues initialValues = new ContentValues();
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
 
}