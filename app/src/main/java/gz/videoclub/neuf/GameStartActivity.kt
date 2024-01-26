package gz.videoclub.neuf

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import androidx.compose.material3.TextButton
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import gz.videoclub.neuf.ui.theme.gradientColors
import gz.videoclub.neuf.ui.theme.gradientColors_green
import gz.videoclub.neuf.ui.theme.gradientColors_red
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.wajahatkarim.flippable.Flippable
import com.wajahatkarim.flippable.FlippableController
import kotlinx.coroutines.delay
import java.util.Locale

class GameStartActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class, DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        sequence_system_initializer()
        val gradient = Brush.horizontalGradient(
            colors = gradientColors
        )

        val gradient_red = Brush.horizontalGradient(
            colors = gradientColors_red
        )

        val gradient_green = Brush.horizontalGradient(
            colors = gradientColors_green
        )

        val passed = intent.getBundleExtra("bundle")?.getBoolean("are30SecPassed")

        val language = Locale.getDefault().language

        isGameOn = true
        minutes = 0
        seconds = -1
        setContent{

            val enabled_array_button_ins: Array<MutableState<Boolean>> = remember{ Array(10){mutableStateOf(true)} }
            val enabled_array_rem: BooleanArray = remember { mutableStateOf(BooleanArray(10) { false }).value }
            var enabled_button_hints by remember { mutableStateOf(false)}

            val button_rem_numberIdentifier: IntArray = remember{IntArray(10){-1}}
            val text_button_rem :Array<String> = remember{Array(10){"?"}}
            val text_button_hints :Array<String> = remember{Array(10){""}}
            val text_mistery: Array<MutableState<String>> = remember{ Array(10, ){mutableStateOf("?")}}
            var sequence_user_first by remember{mutableStateOf( IntArray(10){-1})}

            val flippableControllers_array: Array<FlippableController> = remember{Array(10) { FlippableController() }}

            fun button_remover(index: Int, button_identifier: Int){
                sequence_user[button_identifier] = -1

                text_button_rem[index] = ""
                enabled_array_rem[index] = false

                enabled_array_button_ins[button_identifier].value = true
            }

            fun button_hint_clicker(position_of_TextView_sys: Int, distance: Int){
                val pos1:Int = (position_of_TextView_sys + distance) % 10
                val pos2:Int = (position_of_TextView_sys - distance + 10) % 10
                val num_to_display = sequence_user_first[position_of_TextView_sys]

                text_mistery[pos1].value = num_to_display.toString()
                text_mistery[pos2].value = num_to_display.toString()

                if(pos1 != pos2) {
                    flippableControllers_array[pos1].flip()
                    flippableControllers_array[pos2].flip()

                    Thread.sleep(1500)

                    flippableControllers_array[pos1].flip()
                    flippableControllers_array[pos2].flip()
                }
                else{
                    flippableControllers_array[pos1].flip()

                    Thread.sleep(1500)

                    flippableControllers_array[pos1].flip()
                }
            }


            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color(250, 244, 220))){

                if(passed == true) ImageExample()
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 25.dp)){

                    timer(passed as Boolean)

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp, start = 10.dp, end = 10.dp)
                        ,horizontalArrangement = Arrangement.SpaceBetween){
                            for (i in 0 until 10) {
                                Flippable(
                                    frontSide = {
                                        Box(modifier = Modifier
                                            .width(30.dp)
                                            .aspectRatio(0.7F)) {
                                            buttonGradient(
                                                text = "?",                //mistery buttons
                                                gradient = gradient,
                                                modifier = Modifier.fillMaxSize(),
                                                onClick = {},
                                                RoundedCornerShape(15),
                                                true,
                                                17
                                            )
                                        }
                                    },

                                    backSide = {
                                        Box(modifier = Modifier
                                            .width(30.dp)
                                            .aspectRatio(0.7F)) {
                                            buttonGradient(
                                                text = text_mistery[i].value,                //mistery buttons
                                                gradient = gradient,
                                                modifier = Modifier.fillMaxSize(),
                                                onClick = {},
                                                RoundedCornerShape(15),
                                                true,
                                                17
                                            )
                                        }
                                    },

                                    flipController = flippableControllers_array[i],

                                    // Other optional parameters
                                )
                            }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 70.dp, start = 10.dp, end = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (i in 0 until 10) {
                            buttonGradient_hint(
                                text = text_button_hints[i],               //button_hints
                                gradient = if (text_button_hints[i].contentEquals("0")) gradient_green else gradient_red,
                                modifier = Modifier,
                                onClick = {
                                    GlobalScope.launch {
                                        button_hint_clicker(
                                            i,
                                            text_button_hints[i].toInt()
                                        )
                                    }
                                },
                                RoundedCornerShape(30),
                                enabled = enabled_button_hints,
                                17
                            )
                        }
                    }

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp, start = 10.dp, end = 10.dp)
                        ,horizontalArrangement = Arrangement.SpaceBetween){
                        for(i in 0 until 10){
                            Box(modifier = Modifier
                                .width(30.dp)
                                .aspectRatio(0.7F)){
                                buttonGradient_rem(text = text_button_rem[i]          //button_rem
                                              ,gradient = gradient
                                              ,modifier = Modifier.fillMaxSize()
                                              ,onClick = {button_remover(i, button_rem_numberIdentifier[i])}
                                              ,RoundedCornerShape(15)
                                              ,enabled_array_rem[i]
                                              ,17)
                            }
                        }
                    }

                    fun button_inserter(button_identifier: Int){
                        for(i in 0 until 10){
                            if(enabled_array_rem[i] == false){
                                sequence_user[button_identifier] = i

                                button_rem_numberIdentifier[i] = button_identifier
                                enabled_array_rem[i] = true
                                text_button_rem[i] = "$button_identifier"

                                enabled_array_button_ins[button_identifier].value = false

                                return
                            }
                        }
                        throw NullPointerException("candidate not found in buttonImages_rem, SHOULD NOT HAPPEN")
                    }

                    @Composable
                    fun button_ins(id : Int, number: Int, description: String, enabled: Boolean){
                        TextButton(onClick = {button_inserter(number)}, modifier = Modifier
                            .size(55.dp)
                            , enabled = enabled
                        ){
                            AnimatedVisibility(visible = enabled,
                                enter = scaleIn(),
                                exit = scaleOut()) {
                                Image(painter = painterResource(id), description)
                            }
                        }
                    }

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 85.dp, start = 25.dp, end = 25.dp)
                        ,horizontalArrangement = Arrangement.SpaceBetween){

                        button_ins(if(passed == true) R.drawable.number_0_white else R.drawable.number_0, number = 0, description = "number0", enabled = enabled_array_button_ins[0].value)
                        button_ins( if(passed == true) R.drawable.number_1_white else R.drawable.number_1, number = 1, description = "number1", enabled = enabled_array_button_ins[1].value)
                        button_ins( if(passed == true) R.drawable.number_2_white else R.drawable.number_2, number = 2, description = "number2", enabled = enabled_array_button_ins[2].value)
                        button_ins( if(passed == true) R.drawable.number_3_white else R.drawable.number_3, number = 3, description = "number3", enabled = enabled_array_button_ins[3].value)
                        button_ins( if(passed == true) R.drawable.number_4_white else R.drawable.number_4, number = 4, description = "number4", enabled = enabled_array_button_ins[4].value)
                    }

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 25.dp, start = 25.dp, end = 25.dp)
                    , horizontalArrangement = Arrangement.SpaceBetween){
                        button_ins( if(passed == true) R.drawable.number_5_white else R.drawable.number_5, number = 5, description = "number5", enabled = enabled_array_button_ins[5].value)
                        button_ins( if(passed == true) R.drawable.number_6_white else R.drawable.number_6, number = 6, description = "number6", enabled = enabled_array_button_ins[6].value)
                        button_ins( if(passed == true) R.drawable.number_7_white else R.drawable.number_7, number = 7, description = "number7", enabled = enabled_array_button_ins[7].value)
                        button_ins( if(passed == true) R.drawable.number_8_white else R.drawable.number_8, number = 8, description = "number8", enabled = enabled_array_button_ins[8].value)
                        button_ins( if(passed == true) R.drawable.number_9_white else R.drawable.number_9, number = 9, description = "number9", enabled = enabled_array_button_ins[9].value)
                    }

                    var state by remember {(mutableStateOf(0))}

                    fun is_valid_user_sequence():Boolean{
                        return sequence_user.all {it != -1}
                    }

                    fun submit_proceed(state: Int){
                        if(state == 0){
                            sequence_user_first = sequence_translator(sequence_user)
                            for(i in 0 until 10){

                                val distance: Int = min_distance(sequence_system[i], sequence_user[i])
                                val position_in_sequence_user: Int = sequence_user[i]

                                text_button_hints[position_in_sequence_user] = distance.toString()
                            }
                            enabled_button_hints = true
                        }
                        else if(state == 1){
                            isGameOn = false
                            val victory: Boolean = sequence_user.contentEquals(sequence_system)

                            val b = Bundle()
                            b.putIntArray("sequence_user_final", sequence_user)
                            b.putIntArray("sequence_system", sequence_system)
                            b.putBoolean("outcome", victory)
                            b.putInt("minutes", minutes)
                            b.putInt("seconds", seconds)
                            b.putBoolean("are30SecPassed", passed)
                            val intent = Intent(this@GameStartActivity, RecapActivity::class.java)
                            intent.putExtras(b)
                            startActivity(intent)
                        }
                        else{
                            throw NullPointerException("state has to be either 0 or 1")
                        }
                    }


                    Box(modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center) {
                        buttonGradient( //button_submit
                            when(language){
                                "en" -> getString(R.string.Submit_eng)
                                "it" -> getString(R.string.Submit_ita)
                                else -> getString(R.string.Submit_eng)
                            }
                            , gradient
                            ,
                            Modifier
                                .padding(top = 70.dp, bottom = 45.dp)
                                .size(height = 85.dp, width = 275.dp)
                            ,onClick = {
                                if (!is_valid_user_sequence()) {
                                Toast.makeText(this@GameStartActivity, "incomplete sequence", Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    submit_proceed(state)
                                    state++
                                }
                                       }
                            , CircleShape
                            , true
                            ,30)}
                }
            }
        }
    }

    var sequence_user: IntArray = IntArray(10) {-1}

    private var sequence_system: IntArray = IntArray(10) { it } //array that stores the sequence randomly generated by the system, to be guessed by the user. sequence_system[1] = position of number 1 in the sequence

    private fun sequence_system_initializer(){
        sequence_system.shuffle()
    }

    private fun min_distance(first_num: Int, second_num: Int) : Int{
        val clockwiseDistance = abs(first_num - second_num)
        val counterclockwiseDistance = 10 - clockwiseDistance
        return clockwiseDistance.coerceAtMost(counterclockwiseDistance)
    }

    fun sequence_displayer(sequence: IntArray){
        var string = ""
        for(num in sequence){
            string += num.toString()
        }
        Toast.makeText(this@GameStartActivity, string, Toast.LENGTH_LONG).show()
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun buttonGradient_rem(text: String,
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
            androidx.compose.animation.AnimatedVisibility(visible = enabled,
                enter = scaleIn(),
                exit = scaleOut()) {
                Text(text = text, fontSize = fontSize.sp, color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun buttonGradient_hint(text: String,
                   gradient : Brush,
                   modifier: Modifier = Modifier,
                   onClick: () -> Unit = { },
                   shape: Shape,
                   enabled: Boolean,
                   fontSize: Int
) {
    Box(modifier = Modifier.size(30.dp)) {
        AnimatedVisibility(
            visible = enabled,
            enter = scaleIn()
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
                        .fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Text(text = text, fontSize = fontSize.sp, color = Color.White)
                }
            }
        }
    }
}

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)
val fontFamily = FontFamily(Font(GoogleFont("Playfair Display"), provider))



@Composable
fun timer(passed: Boolean){
    var timeInSeconds by remember{mutableStateOf(0)}

    LaunchedEffect(key1 = timeInSeconds){
        while(isGameOn){
            delay(1000L)
            timeInSeconds ++
        }
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center, ){
        Text(text = time_formatConverter(timeInSeconds), fontFamily = fontFamily, fontSize = 45.sp, fontWeight = FontWeight.Bold, color = if(passed == true) Color.White else Color.Black)
    }
}

fun time_formatConverter(time: Int): String{
   seconds ++
    if(seconds == 60){
        seconds = 0
        minutes ++
    }
    return String.format("%02d:%02d", minutes, seconds)
}

var minutes = 0
var seconds = -1
var isGameOn = true