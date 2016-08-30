package cz.ackee.androidskeleton.model.base;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

/**
 * Base entity that should be stored to database
 * Created by david.bilik@ackee.cz on 26. 6. 2014.
 */
public abstract class BaseDBEntity implements BaseColumns {

    public BaseDBEntity() {
    }

    public BaseDBEntity(Cursor c) {

    }

    public abstract ContentValues getContentValues();

}
