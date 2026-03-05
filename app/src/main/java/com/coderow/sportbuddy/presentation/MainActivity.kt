package com.coderow.sportbuddy.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.coderow.sportbuddy.presentation.exercises.create.CreateExerciseScreen
import com.coderow.sportbuddy.presentation.exercises.list.ExercisesListScreen
import com.coderow.sportbuddy.presentation.mainscreen.MainMenuScreen
import com.coderow.sportbuddy.presentation.mainscreen.model.MainMenuItemType
import com.coderow.sportbuddy.presentation.ui.theme.SportBuddyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SportBuddyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = MainScreen,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<MainScreen> {
                            MainMenuScreen(
                                onMenuItemClick = { type ->
                                    when (type) {
                                        MainMenuItemType.CREATE_EXERCISE -> {
                                            navController.navigate(CreateExercise)
                                        }
                                        MainMenuItemType.EXERCISES_LIST -> {
                                            navController.navigate(ExercisesList)
                                        }
                                        else -> Unit
                                    }
                                }
                            )
                        }

                        composable<CreateExercise> {
                            CreateExerciseScreen(navController)
                        }

                        composable<ExercisesList> {
                            ExercisesListScreen(navController)
                        }
                    }
                }
            }
        }
    }
}

@Serializable
object MainScreen

@Serializable
object CreateExercise

@Serializable
object ExercisesList