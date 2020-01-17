package org.cs.tec.library.Databases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.cs.tec.library.Base.Utils.context
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper

class LibraryDatabases(mContext: Context = context) : ManagedSQLiteOpenHelper(mContext,"db_name", null, 3) {
    //数据库统一在此创建
    override fun onCreate(db: SQLiteDatabase?) {
//        db!!.execSQL("CREATE TABLE IF NOT EXISTS "
//                + table + "( "
//                + FileDownloadModel.ID + " INTEGER PRIMARY KEY, "  // id
//                + FileDownloadModel.URL + " VARCHAR, "  // url
//                + FileDownloadModel.PATH + " VARCHAR, "  // path
//                + FileDownloadModel.STATUS + " TINYINT(7), "  // status
//                + FileDownloadModel.SOFAR + " INTEGER, " // so far bytes
//                + FileDownloadModel.TOTAL + " INTEGER, " // total bytes
//                + FileDownloadModel.ERR_MSG + " VARCHAR, "  // error message
//                + FileDownloadModel.ETAG + " VARCHAR, " // etag
//                + FileDownloadModel.PATH_AS_DIRECTORY + " TINYINT(1) DEFAULT 0, "//path as directory
//                + FileDownloadModel.FILENAME + " VARCHAR, " // path as directory
//                + FileDownloadModel.CONNECTION_COUNT + " INTEGER DEFAULT 1" // connection count
//                + ")");
//        db!!.execSQL("CREATE TABLE IF NOT EXISTS "
//                + CONNECTION_TABLE_NAME + "( "
//                + ConnectionModel.ID + " INTEGER, "
//                + ConnectionModel.INDEX + " INTEGER, "
//                + ConnectionModel.START_OFFSET + " INTEGER, "
//                + ConnectionModel.CURRENT_OFFSET + " INTEGER, "
//                + ConnectionModel.END_OFFSET + " INTEGER, "
//                + "PRIMARY KEY ( " + ConnectionModel.ID + ", " + ConnectionModel.INDEX + " )"
//                + ")")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//        if (oldVersion < 2) {
//            db?.execSQL("ALTER TABLE " + table
//                    + " ADD COLUMN " + FileDownloadModel.PATH_AS_DIRECTORY
//                    + " TINYINT(1) DEFAULT 0")
//            db?.execSQL("ALTER TABLE " + table
//                    + " ADD COLUMN " + FileDownloadModel.FILENAME
//                    + " VARCHAR")
//        } else if (oldVersion < 3) {
//            db?.execSQL("ALTER TABLE " + table
//                    + " ADD COLUMN " + FileDownloadModel.CONNECTION_COUNT
//                    + " INTEGER DEFAULT 1")
//            db?.execSQL("CREATE TABLE IF NOT EXISTS "
//                    + CONNECTION_TABLE_NAME + "( "
//                    + ConnectionModel.ID + " INTEGER, "
//                    + ConnectionModel.INDEX + " INTEGER, "
//                    + ConnectionModel.START_OFFSET + " INTEGER, "
//                    + ConnectionModel.CURRENT_OFFSET + " INTEGER, "
//                    + ConnectionModel.END_OFFSET + " INTEGER, "
//                    + "PRIMARY KEY ( " + ConnectionModel.ID + ", " + ConnectionModel.INDEX + " )"
//                    + ")")
//        }
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.delete(table, null, null);
        db!!.delete(CONNECTION_TABLE_NAME, null, null);
    }

    companion object {
        val table = "demo_table"
        var CONNECTION_TABLE_NAME = "connection_table"
        var mDb: LibraryDatabases? = null

        @Synchronized
        fun getIncetance(context: Context): LibraryDatabases {
            if (mDb == null) {
                mDb = LibraryDatabases(context)
            }
            return mDb!!
        }
    }

}
