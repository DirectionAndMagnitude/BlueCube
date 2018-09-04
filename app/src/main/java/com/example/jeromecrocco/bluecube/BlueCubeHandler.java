package com.example.jeromecrocco.bluecube;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;

public class BlueCubeHandler extends SQLiteOpenHelper {
    //information of database
    private static final int DATABASE_VERSION = 1;


    private static final String DATABASE_NAME =  "study.db";
    public static final String TABLE_NAME =      " study";
    public static final String COLUMN_ID =       "studyID";
    public static final String COLUMN_NAME1 =    "studyName";
    public static final String COLUMN_NAME2 =    "motivation";
    public static final String COLUMN_NAME3 =    "objective";
    public static final String COLUMN_NAME4 =    "expType"; //exp type
    public static final String COLUMN_NAME5 =    "expText"; //exp text
    public static final String COLUMN_NAME6 =    "data3";
    //initialize the database


    public BlueCubeHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = " CREATE TABLE" + TABLE_NAME +
                                "(" +
                                COLUMN_ID + "INTEGER PRIMARYKEY," +
                                COLUMN_NAME1 + " " + "TEXT," + " " +
                                COLUMN_NAME2 + " " + "TEXT," + " " +
                                COLUMN_NAME3 + " " + "TEXT," + " " +
                                COLUMN_NAME4 + " " + "TEXT," + " " +
                                COLUMN_NAME5 + " " + "TEXT," + " " +
                                COLUMN_NAME6 + " " + "TEXT" +  " " +
                                ")";
        db.execSQL(CREATE_TABLE);
    };


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}

    public String loadHandler() {
/*        Use the rawQuery() method of a SQLiteDatabase object to implement
        SQL statement and display result via a Cursor object.*/


        String result = "";
        String query = "Select*FROM" + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            int result_0 = cursor.getInt(0);
            String result_1 = cursor.getString(1);
            result += String.valueOf(result_0) + " " + result_1 +
                    System.getProperty("line.separator");
        }
        cursor.close();
        db.close();
        return result;


    }
    public void addHandler(_study_class study, String datatype) {
/*        To add a new record to the database, we must use the ContentValues
        object with the put() method that is used to assign data to ContentsValues
        object and then use insert() method of SQLiteDatabase object to insert
        data to the database.*/

        //TODO:  How to add exp data later?  new function?  try except?  if !null?
        ContentValues values = new ContentValues();

        if (datatype == "intro")
            values.put(COLUMN_ID,    study.getID()         );
            values.put(COLUMN_NAME1, study.getStudyName()  );
            values.put(COLUMN_NAME2, study.getMotivation() );
            values.put(COLUMN_NAME3, study.getObjective()  );

        if (datatype == "exp")
            values.put(COLUMN_ID,    study.getID()          );
            values.put(COLUMN_NAME4, study.getExpType()     );
            values.put(COLUMN_NAME5, study.getExpText()     );

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();

    }
    public _study_class findHandler(String StudyName) {

/*      We save the result that is returned from implementation of the rawQuery()
        method of the SQLiteDatabase object into a Cursor object and find the
        matching result in this object.*/

        String query = "Select * FROM " + TABLE_NAME + "WHERE" + COLUMN_NAME1 + " = " + "'" + StudyName + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        _study_class study = _study_class.getInstance();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            study.setID(Integer.parseInt(cursor.getString(0)));
            study.setStudyName(cursor.getString(1));
            cursor.close();
        } else {
            study = null;
        }
        db.close();
        return study;

    }
    public boolean deleteHandler(int ID) {
/*        We will save the result that is returned from the implementation of the rawQuery()
        method of the SQLiteDatabase object into a Cursor object and find the matching
        result in this object.
        In the final step, we use the delete() method of the SQLiteDatabase object to delete
        the record.*/

        boolean result = false;
        String query = "Select*FROM" + TABLE_NAME + "WHERE" + COLUMN_ID + "= '" + String.valueOf(ID) + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        _study_class study = _study_class.getInstance();

        if (cursor.moveToFirst()) {
            study.setID(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_NAME, COLUMN_ID + "=?",
                    new String[] {
                            String.valueOf(study.getID())
                    });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    public boolean updateHandler(int ID, String name) {
/*        To update the information of a record, we can use the ContentValues object
        and the update() method of the SQLiteDatabase object*/

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_ID, ID);
        args.put(COLUMN_NAME1, name);
        return db.update(TABLE_NAME, args, COLUMN_ID + "=" + ID, null) > 0;
    }
}


