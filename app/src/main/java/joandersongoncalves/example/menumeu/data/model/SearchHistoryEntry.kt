package joandersongoncalves.example.menumeu.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "search_history")
class SearchHistoryEntry(
    @ColumnInfo(name = "entry_title") @PrimaryKey val entryTitle: String,
    @ColumnInfo(name = "creation_timestamp") val creationTimestamp: Long?
) {

    public fun getTitle(): String {
        return entryTitle;
    }
}