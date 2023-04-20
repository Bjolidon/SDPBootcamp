package com.github.sdp.tarjetakuna.extra

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.StrictMode
import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.github.sdp.tarjetakuna.MainActivity
import com.github.sdp.tarjetakuna.R
import com.github.sdp.tarjetakuna.model.MagicCard
import com.github.sdp.tarjetakuna.model.MagicLayout
import com.github.sdp.tarjetakuna.model.MagicSet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.FileInputStream

/**
 * This class is used to test the export collection function
 */
@RunWith(AndroidJUnit4::class)
class ExportCollectionTest {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var view: View

    private val validCollection = listOf(
        MagicCard(
            "MagicCard",
            "A beautiful card",
            MagicLayout.Normal,
            7,
            "{5}{W}{W}",
            MagicSet("MT15", "Magic 2015"),
            56,
            "https://img.scryfall.com/cards/large/front/1/2/12345678.jpg?1562567890"
        ),
        MagicCard(
            "BestMagicCard",
            "An even more beautiful card",
            MagicLayout.Normal,
            7,
            "{7}{W}{W}",
            MagicSet("MT15", "Magic 2015"),
            56,
            "https://img.scryfall.com/cards/large/front/1/2/12345678.jpg?1562567890"
        )
    )

    @Before
    fun setUp() {
        Intents.init()
        activityScenarioRule.scenario.onActivity {
            view = it.findViewById(R.id.nav_home)
        }
    }

    @After
    fun after() {
        activityScenarioRule.scenario.onActivity {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().permitAll().build()
            )
        }
        Intents.release()
    }

    /**
     * This test checks that the function show a snackbar when the file path is not found
     */
    @Test
    fun filePathProblemShouldShowSnackBar() {
        activityScenarioRule.scenario.onActivity {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().detectDiskReads().penaltyDeath().build()
            )
        }
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText(R.string.menu_exportcollection)).perform(click())

        onView(withText(R.string.ExportCollection_fileDirectoryNotFound))
            .check(matches(isDisplayed()))
    }

    /**
     * This test checks that the function send an intent with the action send
     */
    @Test
    fun clickExportButtonSendAnIntentWithActionSend() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText(R.string.menu_exportcollection)).perform(click())
        intended(hasAction(Intent.ACTION_SEND))
    }

    /**
     * This test checks that the function show a snackbar when the file can't be created
     */
    @Test
    fun fileProblemShowSnackBar() {
        activityScenarioRule.scenario.onActivity {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().detectDiskWrites().penaltyDeath().build()
            )
        }
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText(R.string.menu_exportcollection)).perform(click())

        onView(withText(R.string.ExportCollection_fileCreationFailed))
            .check(matches(isDisplayed()))
    }

    /**
     * This test checks that the function show a snackbar when the intent fails
     */
    @Test
    fun intentFailureShowSnackBar() {
        Intents.intending(hasAction(Intent.ACTION_SEND)).respondWith(null)

        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withText(R.string.menu_exportcollection)).perform(click())

        onView(withText(R.string.ExportCollection_appNotFound))
            .check(matches(isDisplayed()))
    }

    /**
     * This test checks that the function write the right data into the excel file
     */
    @Test
    fun testRightDataAreWrittenIntoExcelFile() {
        ExportCollection.exportCollection(view, validCollection)

        val excelFile = ApplicationProvider.getApplicationContext<Context>()
            .getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.resolve("MyMagicCollection.xls")

        val workbook = HSSFWorkbook(FileInputStream(excelFile))
        val sheet = workbook.getSheetAt(0)

        val headerRow = sheet.getRow(0)
        assertNotNull(headerRow)
        assertEquals("Name", headerRow.getCell(0).stringCellValue)
        assertEquals("Description", headerRow.getCell(1).stringCellValue)
        assertEquals("Mana Cost", headerRow.getCell(2).stringCellValue)
        assertEquals("Set", headerRow.getCell(3).stringCellValue)
        assertEquals("Image URL", headerRow.getCell(4).stringCellValue)

        val dataRow1 = sheet.getRow(1)
        assertNotNull(dataRow1)
        assertEquals("MagicCard", dataRow1.getCell(0).stringCellValue)
        assertEquals("A beautiful card", dataRow1.getCell(1).stringCellValue)
        assertEquals("{5}{W}{W}", dataRow1.getCell(2).stringCellValue)
        assertEquals("Magic 2015", dataRow1.getCell(3).stringCellValue)
        assertEquals(
            "https://img.scryfall.com/cards/large/front/1/2/12345678.jpg?1562567890",
            dataRow1.getCell(4).stringCellValue
        )

        val dataRow2 = sheet.getRow(2)
        assertNotNull(dataRow2)
        assertEquals("BestMagicCard", dataRow2.getCell(0).stringCellValue)
        assertEquals("An even more beautiful card", dataRow2.getCell(1).stringCellValue)
        assertEquals("{7}{W}{W}", dataRow2.getCell(2).stringCellValue)
        assertEquals("Magic 2015", dataRow2.getCell(3).stringCellValue)
        assertEquals(
            "https://img.scryfall.com/cards/large/front/1/2/12345678.jpg?1562567890",
            dataRow2.getCell(4).stringCellValue
        )
    }
}