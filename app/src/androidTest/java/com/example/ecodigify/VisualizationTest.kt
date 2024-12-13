package com.example.ecodigify

import android.view.KeyEvent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.pressKey
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ecodigify.dataclass.Ingredient
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class VisualizationTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun initialization() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        Manager.init(context)

        Manager.ingredientAdd(
            Ingredient(
                id = 1,
                name = "strawberries",
                addDate = LocalDate.now().minusDays(10),
                expirationDate = LocalDate.now().plusDays(10),
                possibleNames = listOf("strawberries"),
                quantity = "1"
            )
        )
    }

    @Test // S-10
    fun recipesVisualization() {
        onView(withId(R.id.navigation_search)).perform(click())
        Thread.sleep(200)

        onView(withId(R.id.search_view)).perform(
            typeText("Romanoff"),
            pressKey(KeyEvent.KEYCODE_ENTER)
        )
        Thread.sleep(200)

        onView(withId(R.id.search_view)).perform(closeSoftKeyboard())
        Thread.sleep(200)

        onView(withId(R.id.recipeRecyclerView)).check(matches(hasMinimumChildCount(1)))
        Thread.sleep(200)

        onView(withId(R.id.filter_button)).perform(click())
        Thread.sleep(200)

        onView(withText("strawberries")).inRoot(isPlatformPopup()).perform(click())
        Thread.sleep(200)

        onView(withId(R.id.recipeRecyclerView)).check(matches(not(hasMinimumChildCount(1))))
    }
}