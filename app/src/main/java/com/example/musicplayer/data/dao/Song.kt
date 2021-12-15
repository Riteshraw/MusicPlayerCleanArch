package com.example.musicplayer.data.dao

import android.os.Parcelable
import android.widget.EditText
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "song_table")
data class Song(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val id: Int /*= 0*/,
        val number: Int,
        val name: String,
        val path: String,
        val folder: String,
        val duration: Int,
        val size: Int,
        val isRecent: Boolean,
        val isFav: Boolean
) : Parcelable {
    constructor(
            number: Int,
            name: String,
            path: String,
            folder: String,
            duration: Int,
            size: Int,
            isRecent: Boolean,
            isFav: Boolean
    ) : this(0, number, name, path, folder, duration, size, isRecent, isFav)

    fun milliSecsToHHMM(view: EditText): String {
        val milisecs = view.text.toString()
        val milliseconds: Long = milisecs as Long

        val minutes = milliseconds / 1000 / 60
        val seconds = milliseconds / 1000 % 60
        val result = minutes as String + ":" + seconds as String
        return result
    }

}

object Converter {
    @JvmStatic
    fun milliSecsToHHMM(view: EditText): String {
        val milisecs = view.text.toString()
        val milliseconds: Long = milisecs as Long

        val minutes = milliseconds / 1000 / 60
        val seconds = milliseconds / 1000 % 60
        val result = minutes as String + ":" + seconds as String
        return result
    }
}
