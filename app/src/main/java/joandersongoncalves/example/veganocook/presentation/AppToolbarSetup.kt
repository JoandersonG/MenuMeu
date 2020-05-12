package joandersongoncalves.example.veganocook.presentation

import android.app.Activity
import androidx.appcompat.widget.Toolbar

class AppToolbarSetup {
    companion object {
        fun setBackButton(appToolbar: Toolbar, activity: Activity) {
            appToolbar.setNavigationOnClickListener { activity.finish() }
        }
    }
}
