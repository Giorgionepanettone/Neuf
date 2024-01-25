package gz.videoclub.neuf

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import gz.videoclub.neuf.ui.theme.gradientColors
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import androidx.compose.foundation.lazy.items

class HistoryActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database---neuf"
        ).build()
        val gameDao = db.gameDao()

        lateinit var listOfGames:  List<Game>

        val job = GlobalScope.launch {
            listOfGames = gameDao.getAll()
        }
        runBlocking {
            job.join()
        }
        val passed = intent.getBundleExtra("bundle")?.getBoolean("are30SecPassed")

        setContent {

            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color(250, 244, 220))) {


                if(passed == true) ImageExample()

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        "History",
                        modifier = Modifier.padding(top = 10.dp),
                        fontSize = 80.sp,
                        fontFamily = fontFamily,
                        color = if(passed == true) Color.White else Color.Black
                    )
                }

                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 150.dp)) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().background(if(passed == true) Color.Transparent else Color.White)
                            //.padding(top = 150.dp)
                    ) {
                        items(listOfGames.reversed()){game ->

                            matchHistory(
                            game.sequence_user,
                            game.sequence_system,
                            game.outcome,
                            game.date.substring(0, 10),
                            timeFormatTranslator(game.time),
                                passed as Boolean
                        )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun matchHistory(user_sequence: IntArray, system_sequence: IntArray, result: Boolean, date: String, time: String, passed: Boolean){
    val text = if(result) "Victory" else "Defeat"
    val color_main_text: Color = if(result) gradientColors[0] else Color(0xdc, 0x14, 0x3c)
    val color_back = if(passed) Color.Transparent else Color(250, 244, 220)

    Box(
        modifier = Modifier
            .padding(top = 5.dp, start = 15.dp, end = 15.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(color_back, RoundedCornerShape(1.dp)) //Color(250, 244, 220)
            .fillMaxWidth()
    )
    {
        Column {
            Text(
                "$text $date time: $time",
                fontSize = 22.sp,
                fontFamily = fontFamily,
                color = color_main_text,
                modifier = Modifier.padding(start = 5.dp, top = 9.dp)
            )

            row_sequence(
                sequence_user_trans = user_sequence,
                sequence_system_trans = system_sequence,
                sequence_to_show = 0,
                0
            )
            row_sequence(
                sequence_user_trans = user_sequence,
                sequence_system_trans = system_sequence,
                sequence_to_show = 1,
                20
            )
        }

    }
}



