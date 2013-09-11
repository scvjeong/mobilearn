package com.example.mobilearn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
 
public class MainProvider {
 
	public static final String KEY_OID_LEARN = "oid_learn";
    public static final String KEY_QUESTION = "question";
    public static final String KEY_OID = "oid";
    private static final String TAG = "MainProvider";
 
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
 
    /**
     *
     * Database creation sql statement
     */
 
    private static final String DATABASE_CREATE = "create table questions (oid integer primary key, "
            + "question text not null, oid_learn interger not null);"
    		+ "create table learn (oid integer primary key, "
    		+ "name text not null, type integer not null, update_date integer not null);";
 
    private static final String DATABASE_NAME = "mobilearn";
    private static final String TABLE_QUESTION = "questions";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;
 
    private static class DatabaseHelper extends SQLiteOpenHelper {
 
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
 
        @Override
        public void onCreate(SQLiteDatabase db) {
        	Log.d("MainProvider", "Provider :: DatabaseHelper:onCreate");
        	db.execSQL("DROP TABLE IF EXISTS learn");
            db.execSQL("DROP TABLE IF EXISTS questions");
            db.execSQL(DATABASE_CREATE);
        }
 
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS learn");
            db.execSQL("DROP TABLE IF EXISTS questions");
            onCreate(db);
        }
    }
 
    public MainProvider(Context ctx) {
        this.mCtx = ctx;
    }
 
    public MainProvider open() throws SQLException {
    	Log.d("MainProvider", "Provider :: MainProvider:open()");
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public MainProvider init() throws SQLException {
    	mDbHelper = new DatabaseHelper(mCtx);
    	mDbHelper.onCreate(mDb);
    	return this;
    }

    public void close() {
        mDbHelper.close();
    }
 
    public long createQuestion(String question, int oid_learn) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_QUESTION, question);
        initialValues.put(KEY_OID_LEARN, oid_learn);
        return mDb.insert(TABLE_QUESTION, null, initialValues);
    }
 
    public boolean deleteQuestion(long rowId) {
        Log.i("Delete called", "value__" + rowId);
        return mDb.delete(TABLE_QUESTION, KEY_OID + "=" + rowId, null) > 0;
    }
 
    public Cursor fetchAllQuestion() {
        return mDb.query(TABLE_QUESTION, new String[] { KEY_OID, KEY_QUESTION }, null, null, null, null, KEY_QUESTION + " ASC");
    }
 
    public Cursor fetchQuestion(long rowId) throws SQLException {
 
        Cursor mCursor = mDb.query(true, TABLE_QUESTION, new String[] { KEY_OID, KEY_QUESTION }, KEY_OID
                + "=" + rowId, null, null, null, null, null);
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
 
}