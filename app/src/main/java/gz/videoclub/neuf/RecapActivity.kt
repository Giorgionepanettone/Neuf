package gz.videoclub.neuf

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import gz.videoclub.neuf.ui.theme.NeufTheme
import gz.videoclub.neuf.ui.theme.gradientColors
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Calendar

class RecapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        var first_time = true
        onBackPressedDispatcher.addCallback(this) {
            if(first_time) {
                Toast.makeText(this@RecapActivity, "Press again to exit", Toast.LENGTH_LONG).show()
                first_time = false
                GlobalScope.launch{
                    delay(3000)
                    first_time = true
                }
            }
            else{
                finishAffinity()
            }
        }
        val bundle = intent.extras
        val time = bundle?.getInt("minutes")?.times(60)?.plus(bundle.getInt("seconds")) as Int
        val outcome =  bundle.getBoolean("outcome")
        val sequence_user = bundle.getIntArray("sequence_user_final") as IntArray
        val sequence_system = bundle.getIntArray("sequence_system") as IntArray
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-dd-MM HH:mm")
        val current = formatter.format(date)
        val passed = bundle.getBoolean("are30SecPassed")

        var game = Game(0, time, outcome, sequence_user, sequence_system, current)
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database---neuf"
        ).build()
        val gameDao = db.gameDao()

        val provider = GoogleFont.Provider(
            providerAuthority = "com.google.android.gms.fonts",
            providerPackage = "com.google.android.gms",
            certificates = R.array.com_google_android_gms_fonts_certs
        )
        val fontFamily = FontFamily(Font(GoogleFont("Playfair Display"), provider))

        lateinit var bestGame: Game
        val job = GlobalScope.launch {
            insertOrUpdateGame(game, gameDao)
            bestGame = gameDao.findBestGame()
        }
        runBlocking {
            job.join()
        }
        setContent{
            NeufTheme{
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color(250,244,220))){

                    if(passed) ImageExample()

                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 20.dp, start = 16.dp, end = 16.dp, bottom = 20.dp)){

                        val outcome = bundle.getBoolean("outcome")

                        val main_text = if(outcome) "Victory" else "Defeat"
                        val color_main_text = if(outcome) gradientColors[0] else Color(0xdc, 0x14, 0x3c)

                        val gradient = Brush.horizontalGradient(
                            colors = gradientColors
                        )

                        Text(main_text, modifier = Modifier.fillMaxWidth(),fontSize = 100.sp, textAlign = TextAlign.Center,
                            color = color_main_text, fontFamily = fontFamily
                        )

                        Text(text = if(bestGame.outcome) "Best time = " + timeFormatTranslator(bestGame.time) else "", fontSize = 30.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(start = 7.dp, end = 7.dp))
                        Text(text =  "Your time = " + timeFormatTranslator(game.time), fontSize = 30.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(start = 7.dp, end = 7.dp ,top = 10.dp), color = if(passed) Color.White else Color.Black)

                        val sequence_user_trans = sequence_translator(bundle.getIntArray("sequence_user_final") as IntArray)
                        val sequence_system_trans = sequence_translator(bundle.getIntArray("sequence_system") as IntArray)

                        Text("Your sequence:", fontSize = 30.sp, modifier = Modifier.padding(start = 7.dp, top = 20.dp), color = if(passed) Color.White else Color.Black)
                        row_sequence(sequence_user_trans, sequence_system_trans, 0, 0)

                        Text("Mistery sequence:", fontSize = 30.sp, modifier = Modifier.padding(start = 7.dp, top = 20.dp), color = if(passed) Color.White else Color.Black)
                        row_sequence(sequence_user_trans, sequence_system_trans, 1, 0)


                        val context = LocalContext.current
                        Box(modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center) {

                            buttonGradient("Try again"
                                , gradient
                                ,
                                Modifier
                                    .padding(top = 65.dp)
                                    .size(height = 85.dp, width = 275.dp)
                                , onClick = {
                                    var bundlee = Bundle()
                                    bundlee.putBoolean("are30SecPassed", passed)
                                    val intent = Intent(context, GameStartActivity::class.java)
                                    intent.putExtra("bundle", bundlee)
                                    startActivity(intent)
                            }, CircleShape
                            ,true
                            ,21)
                        }

                        Box(modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center) {
                            buttonGradient("Main Menu"
                                , gradient
                                , Modifier
                                    .padding(top = 40.dp)
                                    .size(height = 85.dp, width = 275.dp)
                                ,onClick = {
                                    var bundlee = Bundle()
                                    bundlee.putBoolean("are30SecPassed", passed)
                                    val intent = Intent(context, MainActivity::class.java)
                                    intent.putExtra("bundle", bundlee)
                                    startActivity(intent)
                                }, CircleShape
                                ,true
                                ,21)
                        }

                    }

                }
            }
        }
    }

}


@Composable
fun row_sequence(sequence_user_trans: IntArray, sequence_system_trans: IntArray, sequence_to_show: Int, padding: Int){ //sequence_to_show : 0 = user_sequence, 1 = system_sequence


    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 10.dp, start = 4.dp, bottom = padding.dp), horizontalArrangement = Arrangement.SpaceEvenly){
        for(i in 0 until 10){
            var int = 0
            var color = Color.Unspecified
            if(sequence_to_show == 0) {
                int = sequence_user_trans[i]
                color = if (int == sequence_system_trans[i]) gradientColors[0] else gradientColors[1] //(238, 75, 43)
            }
            else if(sequence_to_show == 1){
                int = sequence_system_trans[i]
                color = gradientColors[0]
            }
                Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(color, shape = CircleShape) // Set the size of the circle
                    .aspectRatio(1f)

            ) {
                Text(
                    text = "$int",
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 3.dp),// Adjust padding as needed,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun buttonGradient(text: String,
                   gradient : Brush,
                   modifier: Modifier = Modifier,
                   onClick: () -> Unit = { },
                   shape: Shape,
                   enabled: Boolean,
                   fontSize: Int
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        contentPadding = PaddingValues(),
        onClick = { onClick() },
        shape = shape,
        enabled = enabled
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .fillMaxSize()
                , contentAlignment = Alignment.Center
        ) {
            Text(text = text, fontSize = fontSize.sp, color = Color.White)
        }
    }
}




fun sequence_translator(sequence : IntArray): IntArray{ //translates sequence from a value:position format to position:value
    val sequence_translated = IntArray(sequence.size)
    for(i in sequence.indices){
        sequence_translated[sequence[i]] = i
    }
    return sequence_translated
}


@Entity(tableName = "game")
data class Game(@PrimaryKey(autoGenerate = true) var id: Int,
                @ColumnInfo("time") var time: Int,
                @ColumnInfo("outcome") var outcome: Boolean,
                @ColumnInfo("sequence_user") var sequence_user : IntArray,
                @ColumnInfo("sequence_system") var sequence_system: IntArray,
                @ColumnInfo("date") var date: String ){
}

@Dao
interface GameDao { //current Best Game is at id 1
    @Query("SELECT * FROM game")
    fun getAll(): List<Game>

    @Query("SELECT * FROM game WHERE id = 1")
    fun findBestGame(): Game

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(game: Game)
}

@TypeConverters(IntArrayConverter::class)
@Database(entities = [Game::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}

class IntArrayConverter {

    @TypeConverter
    fun fromIntArray(intArray: IntArray): String {
        return intArray.joinToString(",")
    }

    @TypeConverter
    fun toIntArray(intArrayString: String): IntArray {
        return intArrayString.split(",").map { it.toInt() }.toIntArray()
    }
}


fun insertOrUpdateGame(game: Game, dao: GameDao) {
    var bestGame = dao.findBestGame()

    if(bestGame == null || !game.outcome){
        dao.insert(game)
        return
    }

    if(bestGame.outcome == false || game.time < bestGame.time){
        game.id = 1
        dao.insert(game)
        bestGame.id = 0
        dao.insert(bestGame)
    }
    else{
        dao.insert(game)
    }
}

fun timeFormatTranslator(totalTimeInSeconds: Int): String{
    minutes = totalTimeInSeconds / 60
    seconds = totalTimeInSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

