package de.svbrockscheid.model;

import android.provider.BaseColumns;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.annotations.AutoIncrement;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Key;
import se.emilsjolander.sprinkles.annotations.Table;

/**
 * This table contains modification dates for the various requested files
 */
@Table("FileModification")
public class FileModification extends Model {

    public static final String COLUMN_FILE_NAME = "fileName";
    public static final String COLUMN_MODIFICATION_DATE = "modification";
    public static final String TABLE_NAME = "FileModification";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            BaseColumns._ID + " INTEGER PRIMARY KEY, " +
            COLUMN_FILE_NAME + " TEXT, " +
            COLUMN_MODIFICATION_DATE + " INTEGER)";
    @Column(BaseColumns._ID)
    @Key
    @AutoIncrement
    private long id;
    @Column(COLUMN_FILE_NAME)
    private String fileName;
    @Column(COLUMN_MODIFICATION_DATE)
    private Long modificationDate;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Long modificationDate) {
        this.modificationDate = modificationDate;
    }
}
