package com.github.bjolidon.bootcamp

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.bjolidon.bootcamp.ui.authentication.AuthenticationActivity
import com.github.bjolidon.bootcamp.ui.authentication.SignOutActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignOutActivityTest {

    @Test
    fun testGreetingActivitySignOut() {
        val intent = Intent(getApplicationContext(), SignOutActivity::class.java)
        val activity = ActivityScenario.launch<SignOutActivity>(intent)
        Intents.init()
        onView(withId(R.id.signOutButton)).perform(click())
        Intents.intended(hasComponent(AuthenticationActivity::class.java.name))
        Intents.release()
        activity.close()
    }
}