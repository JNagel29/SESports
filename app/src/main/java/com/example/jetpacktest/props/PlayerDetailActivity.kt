package com.example.jetpacktest.props

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.jetpacktest.R
import com.example.jetpacktest.props.network.models.GameOdds
import com.example.jetpacktest.props.viewmodel.DetailOddsUiState
import com.example.jetpacktest.props.viewmodel.DetailOddsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlayerDetailActivity : AppCompatActivity() {

    private val vm: DetailOddsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_detail)

        // 1) Read the event ID
        val eventId = intent.getStringExtra("eventId")
            ?: return finish()

        // 2) Kick off loading
        vm.load(eventId)

        // 3) Find header views
        val nameTv        = findViewById<TextView>(R.id.playerNameText)
        val infoTv        = findViewById<TextView>(R.id.playerInfoText)
        val nextGameTv    = findViewById<TextView>(R.id.playerNextGameText)
        val oddsContainer = findViewById<LinearLayout>(R.id.oddsContainer)

        // 4) Observe UI state
        lifecycleScope.launch {
            vm.uiState.collectLatest { state ->
                // clear out old odds views
                oddsContainer.removeAllViews()

                when (state) {
                    DetailOddsUiState.Loading -> {
                        // TODO: show a ProgressBar if you like
                    }
                    is DetailOddsUiState.Error -> {
                        nameTv.text = state.message
                    }
                    is DetailOddsUiState.Success -> {
                        val game: GameOdds = state.game

                        // 5) Populate header using your format strings
                        nameTv.text     = getString(R.string.player_vs_format, game.homeTeam, game.awayTeam)
                        infoTv.text     = ""    // (no additional info for a "game")
                        nextGameTv.text = ""    // (or you could show commenceTime)

                        // 6) One block per bookmaker
                        for (book in game.bookmakers) {
                            val bookView = layoutInflater.inflate(
                                R.layout.item_bookmaker,
                                oddsContainer,
                                false
                            )

                            bookView.findViewById<TextView>(R.id.bookieTitle)
                                .text = book.title

                            val marketsContainer = bookView.findViewById<LinearLayout>(R.id.marketsContainer)

                            // 7) One block per market
                            for (mkt in book.markets) {
                                val mktView = layoutInflater.inflate(
                                    R.layout.item_market,
                                    marketsContainer,
                                    false
                                )
                                mktView.findViewById<TextView>(R.id.marketName)
                                    .text = mkt.key

                                val outcomesContainer = mktView.findViewById<LinearLayout>(R.id.outcomesContainer)

                                // 8) One block per outcome
                                for (out in mkt.outcomes) {
                                    val oView = layoutInflater.inflate(
                                        R.layout.item_outcome,
                                        outcomesContainer,
                                        false
                                    )
                                    oView.findViewById<TextView>(R.id.outName)
                                        .text = out.name
                                    oView.findViewById<TextView>(R.id.outPrice)
                                        .text = out.price.toString()

                                    // 9) Only show “Pts: X.X” if non‑null
                                    out.point?.let { pts ->
                                        oView.findViewById<TextView>(R.id.outPoint)
                                            .text = getString(R.string.player_points_format, pts)
                                    }

                                    outcomesContainer.addView(oView)
                                }

                                marketsContainer.addView(mktView)
                            }

                            oddsContainer.addView(bookView)
                        }
                    }
                }
            }
        }
    }
}
