package com.example.ecodigify

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test // S-03
    fun navigation() {
        onView(withId(R.id.navigation_search)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.search_container)).check(matches(isDisplayed()))
        onView(withId(R.id.search_view)).check(matches(isDisplayed()))
        onView(withId(R.id.filter_button)).check(matches(isDisplayed()))

        onView(withId(R.id.navigation_favourites)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.favouritesRecyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.text_favourites)).check(matches(isDisplayed()))

        onView(withId(R.id.navigation_ingredients)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.text_ingredients)).check(matches(isDisplayed()))
    }
}