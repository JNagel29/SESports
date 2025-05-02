package com.example.jetpacktest.propsBaseball.ui

data class StaticPlayerProp(
    val player: String,
    val selections: List<Selection>
)

data class Selection(
    val label: String,
    val books: List<BookOutcome>
)

data class BookOutcome(
    val bookie: String,
    val outcome: OutcomeDetail
)

data class OutcomeDetail(
    val line: Double,
    val cost: Double
)

object StaticBaseballPropsData {
    val propsByStat = mapOf(
        "Strikeouts" to listOf(
            StaticPlayerProp(
                player = "Tyler Glasnow",
                selections = listOf(
                    Selection("Over", listOf(
                        BookOutcome("DraftKings", OutcomeDetail(6.5, -142.0)),
                        BookOutcome("FanDuel", OutcomeDetail(6.5, -155.0)),
                        BookOutcome("BetMGM", OutcomeDetail(6.5, -148.0))
                    )),
                    Selection("Under", listOf(
                        BookOutcome("DraftKings", OutcomeDetail(6.5, -110.0)),
                        BookOutcome("FanDuel", OutcomeDetail(6.5, -105.0)),
                        BookOutcome("BetMGM", OutcomeDetail(6.5, -110.0))
                    ))
                )
            ),
            StaticPlayerProp(
                player = "Gerrit Cole",
                selections = listOf(
                    Selection("Over", listOf(
                        BookOutcome("DraftKings", OutcomeDetail(7.5, -105.0)),
                        BookOutcome("FanDuel", OutcomeDetail(7.5, -110.0)),
                        BookOutcome("BetMGM", OutcomeDetail(7.5, -102.0))
                    )),
                    Selection("Under", listOf(
                        BookOutcome("DraftKings", OutcomeDetail(7.5, -115.0)),
                        BookOutcome("FanDuel", OutcomeDetail(7.5, -105.0)),
                        BookOutcome("BetMGM", OutcomeDetail(7.5, -110.0))
                    ))
                )
            )
        ),
        "Hits" to listOf(
            StaticPlayerProp("Shohei Ohtani", selections = hitSelections(1.5)),
            StaticPlayerProp("Aaron Judge", selections = hitSelections(1.5)),
            StaticPlayerProp("Giancarlo Stanton", selections = hitSelections(0.5)),
            StaticPlayerProp("Freddie Freeman", selections = hitSelections(1.5)),
            StaticPlayerProp("Will Smith", selections = hitSelections(0.5))
        ),
        "Home Runs" to listOf(
            StaticPlayerProp("Shohei Ohtani", selections = homeRunSelections(0.5)),
            StaticPlayerProp("Aaron Judge", selections = homeRunSelections(0.5)),
            StaticPlayerProp("Giancarlo Stanton", selections = homeRunSelections(0.5)),
            StaticPlayerProp("Freddie Freeman", selections = homeRunSelections(0.5)),
            StaticPlayerProp("Will Smith", selections = homeRunSelections(0.5))
        ),
        "RBIs" to listOf(
            StaticPlayerProp("Shohei Ohtani", selections = rbiSelections(0.5)),
            StaticPlayerProp("Aaron Judge", selections = rbiSelections(0.5)),
            StaticPlayerProp("Giancarlo Stanton", selections = rbiSelections(0.5)),
            StaticPlayerProp("Freddie Freeman", selections = rbiSelections(0.5)),
            StaticPlayerProp("Will Smith", selections = rbiSelections(0.5))
        )
    )

    private fun hitSelections(line: Double) = listOf(
        Selection("Over", listOf(
            BookOutcome("DraftKings", OutcomeDetail(line, -1720.0)),
            BookOutcome("FanDuel", OutcomeDetail(line, -148.0)),
            BookOutcome("BetMGM", OutcomeDetail(line, -118.0))
        )),
        Selection("Under", listOf(
            BookOutcome("DraftKings", OutcomeDetail(line, 100.0)),
            BookOutcome("FanDuel", OutcomeDetail(line, 105.0)),
            BookOutcome("BetMGM", OutcomeDetail(line, 110.0))
        ))
    )

    private fun homeRunSelections(line: Double) = listOf(
        Selection("Over", listOf(
            BookOutcome("DraftKings", OutcomeDetail(line, 180.0)),
            BookOutcome("FanDuel", OutcomeDetail(line, 164.0)),
            BookOutcome("BetMGM", OutcomeDetail(line, 225.0))
        )),
        Selection("Under", listOf(
            BookOutcome("DraftKings", OutcomeDetail(line, -241.0)),
            BookOutcome("FanDuel", OutcomeDetail(line, -190.0)),
            BookOutcome("BetMGM", OutcomeDetail(line, -207.0))
        ))
    )

    private fun rbiSelections(line: Double) = listOf(
        Selection("Over", listOf(
            BookOutcome("DraftKings", OutcomeDetail(line, -115.0)),
            BookOutcome("FanDuel", OutcomeDetail(line, -170.0)),
            BookOutcome("BetMGM", OutcomeDetail(line, -136.0))
        )),
        Selection("Under", listOf(
            BookOutcome("DraftKings", OutcomeDetail(line, -113.0)),
            BookOutcome("FanDuel", OutcomeDetail(line, -100.0)),
            BookOutcome("BetMGM", OutcomeDetail(line, 100.0))
        ))
    )
}
