import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spending")
data class Spending(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val amount: Double
)
