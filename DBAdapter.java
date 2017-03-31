package vcs.com.demoall;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vcsdev0103 on 30-03-2017.
 */

public class DBAdapter {

   private static String TABLE_CREATE_City_Mst = "CREATE TABLE "
     + "City_Mst" + " ("
     + "intGlCode " + " Integer Primary Key ,"
     + "varCityCode" + " Text,"
     + "fk_StateGlCode" + " Integer,"
     + "varCityName" + " Text)";

   private DatabaseHelper DBHelper;
   private SQLiteDatabase db;

   public DBAdapter(Context ctx) {
      DBHelper = new DatabaseHelper(ctx);
   }

   public DBAdapter open() throws SQLException {
      db = DBHelper.getWritableDatabase();
      return this;
   }

   public void close() {
      DBHelper.close();
   }

   public class DatabaseHelper extends SQLiteOpenHelper {

      DatabaseHelper(Context context) {
         super(context, "ABC", null, 1);
      }

      @Override
      public void onCreate(SQLiteDatabase db) {
         db.execSQL(TABLE_CREATE_City_Mst);
      }

      @Override
      public void onUpgrade(SQLiteDatabase db, int i, int i1) {

      }
   }
}
