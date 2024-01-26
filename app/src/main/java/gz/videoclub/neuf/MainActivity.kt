package gz.videoclub.neuf

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import gz.videoclub.neuf.ui.theme.gradientColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale


class MainActivity : ComponentActivity() {
    private var are30SecPassed by mutableStateOf(false)
    private var timePassed by mutableStateOf(-1)

    override fun onResume() {
        super.onResume()
        timePassed = -1
    }

    private fun timer() {
        if(!are30SecPassed) {
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    while (true) {
                        timePassed++
                        if (timePassed > 29) {
                            withContext(Dispatchers.Main) {
                                are30SecPassed = true
                            }
                            break
                        }
                        delay(1000)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        val language = Locale.getDefault().language

        val gradient = Brush.horizontalGradient(
            colors = gradientColors
        )
        val provider = GoogleFont.Provider(
            providerAuthority = "com.google.android.gms.fonts",
            providerPackage = "com.google.android.gms",
            certificates = R.array.com_google_android_gms_fonts_certs
        )
        val fontFamily = FontFamily(Font(GoogleFont("Playfair Display"), provider))

        val bundlee = intent.getBundleExtra("bundle")
        if(bundlee != null){
            are30SecPassed = bundlee.getBoolean("are30SecPassed")
        }

        setContent {

            val context = LocalContext.current
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color(250, 244, 220))
            )
            {

                if(are30SecPassed) ImageExample()

                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(all = 20.dp)) {



                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                        Text("Nine", modifier = Modifier.padding(top = 40.dp), fontSize = 100.sp, fontFamily = fontFamily, color = if(are30SecPassed) Color.White else Color.Black)
                    }


                    Box(modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center) {


                        buttonGradient(
                            when(language){
                                "en" -> getString(R.string.History_eng)
                                "it" -> getString(R.string.History_ita)
                                else -> getString(R.string.History_eng)
                            }
                            , gradient
                            , Modifier
                                .padding(top = 250.dp)
                                .size(height = 85.dp, width = 275.dp)
                            ,onClick = {
                                val bundle = Bundle()
                                bundle.putBoolean("are30SecPassed", are30SecPassed)
                                val intent = Intent(context, HistoryActivity::class.java)
                                intent.putExtra("bundle", bundle)
                                startActivity(context, intent, null)
                            }, CircleShape
                            ,true
                            ,30)
                    }


                    Box(modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center) {


                        buttonGradient(
                            when(language){
                                "en" -> getString(R.string.Start_eng)
                                "it" -> getString(R.string.Start_ita)
                                else -> getString(R.string.Start_eng)
                                          }
                            , gradient
                            , Modifier
                                .padding(top = 50.dp)
                                .size(height = 85.dp, width = 275.dp)
                            ,onClick = {
                                val bundle = Bundle()
                                bundle.putBoolean("are30SecPassed", are30SecPassed)
                                val intent = Intent(context, GameStartActivity::class.java)
                                intent.putExtra("bundle", bundle)
                                startActivity(context, intent, bundle)
                            }, CircleShape
                            ,true
                            ,30)
                    }
                }
            }
        }
        timer()
}
}

@Composable
fun ImageExample() { //function taken from stackoverflow
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Image(
        painter = rememberAsyncImagePainter(R.drawable.sky_gif0, imageLoader),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillHeight
    )
}

