package com.example.musicplayer.data.dataProviders

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.example.musicplayer.data.dao.Song
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongDataProvider @Inject constructor(private val context: Context) {

    fun getSongsList(): List<Song> {
        var songArrayList = arrayListOf<Song>()

        /**
         * All the audio files can be accessed using the below initialised musicUri.
         * And there is a cursor to iterate over each and every column.
         */
        val contentResolver: ContentResolver = context.contentResolver
        val musicUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val order = MediaStore.Audio.Media._ID + " " + "asc"
        /*val musicCursor: Cursor? = contentResolver.query(
            musicUri, null, null,
            null, order, null
        )*/
        val musicCursor: Cursor? = contentResolver.query(
            musicUri, null, MediaStore.Audio.Media.IS_MUSIC + " =1",
            null, order, null
        )

        // If cursor is not null
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get Columns
            val id: Int = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val title: Int = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val name: Int = musicCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
            val uri: Int = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val duration: Int = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val size: Int = musicCursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
            val music: Int = musicCursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)
            // Store the title, id and artist name in Song Array list.
            do {
                //if (musicCursor.getInt(music) == 1) {
                val id: Int = musicCursor.getInt(id)
                val title: String = musicCursor.getString(title)
                val name: String = musicCursor.getString(name)
                val uri: String = musicCursor.getString(uri)
                val last = (uri.toString()).split('/').lastIndex
                val folder: String = (uri.toString()).split('/')[last - 1]
                val duration: Int = musicCursor.getInt(duration)
                val size: Int = musicCursor.getInt(size)
                // Add the info to our array.
                songArrayList.add(
                    Song(
                        /*0,*/ id, title, uri, folder, duration, size, false, false
                    )
                )
                //}
            } while (musicCursor.moveToNext())

            // For best practices, close the cursor after use.
            musicCursor.close()
        }
        return songArrayList
    }
}