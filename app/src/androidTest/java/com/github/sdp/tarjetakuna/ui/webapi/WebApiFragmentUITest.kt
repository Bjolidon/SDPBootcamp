package com.github.sdp.tarjetakuna.ui.webapi

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.sdp.tarjetakuna.R
import com.github.sdp.tarjetakuna.utils.FileReader
import com.github.sdp.tarjetakuna.utils.OkHttp3IdlingResource
import com.github.sdp.tarjetakuna.utils.OkHttpProvider
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WebApiFragmentUITest {

    private lateinit var scenario: FragmentScenario<WebApiFragment>
    private val mockWebServer = MockWebServer()
    private lateinit var okHttp3IdlingResource: OkHttp3IdlingResource

    @Before
    fun setUp() {
        Intents.init()
        scenario = launchFragmentInContainer()

        // setup mock webserver
        okHttp3IdlingResource = OkHttp3IdlingResource.create(
            "okhttp",
            OkHttpProvider.getOkHttpClient()
        )
        IdlingRegistry.getInstance().register(okHttp3IdlingResource)
        mockWebServer.start(8080)
    }

    @After
    fun after() {
        Intents.release()
        scenario.close()

        // teardown mock webserver
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(okHttp3IdlingResource)
    }

    @Test
    fun test_initialText() {
        onView(withId(R.id.api_cards)).check(matches(withText(R.string.api_cards)))
        onView(withId(R.id.api_sets)).check(matches(withText(R.string.api_sets)))
        onView(withId(R.id.api_results)).check(matches(withText(R.string.api_default_results)))
        onView(withId(R.id.api_back_home)).check(matches(withText(R.string.go_back_to_home_page)))
    }

    @Test
    fun test_clickOnCardsButton() {
        onView(withId(R.id.api_cards)).perform(click())
        onView(withId(R.id.api_results)).check(matches(withText(R.string.api_waiting_results)))
    }

    @Test
    fun test_clickOnSetsButton() {
        onView(withId(R.id.api_sets)).perform(click())
        onView(withId(R.id.api_results)).check(matches(withText(R.string.api_waiting_results)))
    }

    @Test
    fun test_clickOnCardsButtonWithMockWebServer() {
        // setup the api to use the mock webserver
        WebApi.magicUrl = "http://127.0.0.1:8080"

        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse().setResponseCode(200)
                    .setBody(FileReader.readStringFromFile("magic_webapi_cards_response.json"))
            }
        }

        onView(withId(R.id.api_cards)).perform(click())
        onView(withId(R.id.api_results)).check(matches(withSubstring("DataCard(name='Ancestor's Chosen', manaCost='{5}{W}{W}'")))
        onView(withId(R.id.api_results)).check(matches(withSubstring("DataCard(name='Angel of Mercy', manaCost='{4}{W}', colors=W")))
        onView(withId(R.id.api_results)).check(matches(withSubstring("DataCard(name='Angelic Blessing', manaCost='{2}{W}', colors=W")))
    }

    @Test
    fun test_clickOnSetsButtonWithMockWebServer() {
        // setup the api to use the mock webserver
        WebApi.magicUrl = "http://127.0.0.1:8080"

        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse().setResponseCode(200)
                    .setBody(FileReader.readStringFromFile("magic_webapi_sets_response.json"))
            }
        }

        onView(withId(R.id.api_sets)).perform(click())
        onView(withId(R.id.api_results)).check(matches(withSubstring("DataSet(code='2ED', name='Unlimited Edition', type='core', releaseDate=1993-12-01")))
        onView(withId(R.id.api_results)).check(matches(withSubstring("DataSet(code='2X2', name='Double Masters 2022', type='masters', releaseDate=2022-07-08")))
        onView(withId(R.id.api_results)).check(matches(withSubstring("DataSet(code='2XM', name='Double Masters', type='masters', releaseDate=2020-08-07")))
        onView(withId(R.id.api_results)).check(matches(withSubstring("DataSet(code='3ED', name='Revised Edition', type='core', releaseDate=1994-04-01")))
        onView(withId(R.id.api_results)).check(matches(withSubstring("DataSet(code='40K', name='Warhammer 40,000', type='commander', releaseDate=2022-08-12")))
    }
}
