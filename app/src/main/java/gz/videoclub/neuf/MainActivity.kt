package gz.videoclub.neuf

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gz.videoclub.neuf.ui.theme.gradientColors

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        val gradient = Brush.horizontalGradient(
            colors = gradientColors
        )
        val provider = GoogleFont.Provider(
            providerAuthority = "com.google.android.gms.fonts",
            providerPackage = "com.google.android.gms",
            certificates = R.array.com_google_android_gms_fonts_certs
        )
        val fontFamily = FontFamily(Font(GoogleFont("Playfair Display"), provider))
        setContent {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xff, 0xfd, 0xfa))) {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(all = 20.dp)) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                        Text("Nine", modifier = Modifier.padding(top = 40.dp), fontSize = 100.sp, fontFamily = fontFamily)
                    }

                    val context = LocalContext.current
                    Box(modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center) {
                        buttonGradient("Start"
                            , gradient
                            , Modifier
                                .padding(top = 400.dp)
                                .size(height = 85.dp, width = 275.dp)
                            ,onClick = {
                                val intent = Intent(context, GameStartActivity::class.java)
                                startActivity(intent)
                            }, CircleShape
                            ,true
                            ,30)
                    }
                }
            }
        }

    }
}

class Clicker(private val context: Context): View.OnClickListener{
    override fun onClick(view: View?){
        if(view == null) return
        val intent = Intent(context, GameStartActivity::class.java)
        context.startActivity(intent)
    }
}
