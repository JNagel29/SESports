package com.example.jetpacktest.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import com.example.jetpacktest.models.TeamStanding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StandingBracketPager(
    westernFlow: Flow<List<TeamStanding>>,
    easternFlow: Flow<List<TeamStanding>>,
    navigateToTeamProfile: (String) -> Unit,
    updateStandingsByYear: (String) -> Unit,
    yearOptions: List<String>
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val titles = listOf("Standings", "Bracket")
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            titles.forEachIndexed { index, title ->
                Tab(
                    text = {
                        Text(
                            text = title,
                            color = Color.Black,
                            fontFamily = FontFamily.Serif,
                        )
                    },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(page = index)
                        }
                    }
                )
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false
            ) { page ->
                when (page) {
                    0 -> StandingsScreen(
                        westernFlow = westernFlow,
                        easternFlow = easternFlow,
                        navigateToTeamProfile = navigateToTeamProfile,
                        updateStandingsByYear = updateStandingsByYear,
                        yearOptions = yearOptions
                    )
                    1 -> BracketsScreen()
                }
            }
        }
    }
}