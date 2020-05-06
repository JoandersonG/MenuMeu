package joandersongoncalves.example.veganocook.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.data.ApiService
import joandersongoncalves.example.veganocook.data.model.Video
import joandersongoncalves.example.veganocook.data.response.VideoBodyResponse
import kotlinx.android.synthetic.main.activity_recipe_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : YouTubeBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

    }
}
