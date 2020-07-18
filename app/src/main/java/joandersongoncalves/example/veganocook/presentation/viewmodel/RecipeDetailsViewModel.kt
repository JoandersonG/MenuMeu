package joandersongoncalves.example.veganocook.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import joandersongoncalves.example.veganocook.data.model.Recipe

class RecipeDetailsViewModel(application: Application) : AndroidViewModel(application) {

    val recipe = MutableLiveData<Recipe>()
}