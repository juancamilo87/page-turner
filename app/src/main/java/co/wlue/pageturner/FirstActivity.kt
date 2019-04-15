package co.wlue.pageturner

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import co.wlue.pageturner.utils.loadMXLFromRes
import kotlinx.android.synthetic.main.first_activity.*
import uk.co.dolphin_com.seescoreandroid.CursorView
import uk.co.dolphin_com.seescoreandroid.SeeScoreView

class FirstActivity: AppCompatActivity() {

    private val cursorView: CursorView by lazy {
        CursorView(this, object: CursorView.OffsetCalculator {
            override fun getScrollY(): Float {
                return scroll_view.scrollY.toFloat()
            }

        })
    }

    private val ssView: SeeScoreView by lazy {
        SeeScoreView(this, cursorView, assets, null, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.first_activity)

        scroll_view.addView(ssView)
        loadScore()

    }

    private fun loadScore() {
        val score = loadMXLFromRes(this, R.raw.totoro)
//        val score = loadMXLFromRes(this, R.raw.test)
        ssView.setLayoutCompletionHandler {
            //Do something when the score finished loading
        }
        ssView.setScore(score, emptyList(), 1F)
        score_title.text = score.header.work_title
        score_composer.text = score.header.composer
    }

    init {
        /**
         * load the SeeScoreLib.so library
         */
        System.loadLibrary("stlport_shared")
        System.loadLibrary("SeeScoreLib")
    }

}