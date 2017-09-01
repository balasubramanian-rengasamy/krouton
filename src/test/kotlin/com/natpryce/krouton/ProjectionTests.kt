package com.natpryce.krouton

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import java.util.Date


class ProjectionTests {
    object MillisSinceEpoch : Projection<Long, Date> {
        override fun fromParts(parts: Long): Date? = Date(parts)
        override fun toParts(mapped: Date) = mapped.time
    }

    val timestamps = long asA MillisSinceEpoch

    @Test
    fun route_for_abstracted_scalar() {
        assertThat(timestamps.parse("/30"), equalTo(Date(30)))
    }

    @Test
    fun reverse_routing_for_abstracted_scalar() {
        assertThat(timestamps.path(Date(1000)), equalTo("/1000"))
    }


    data class Score(val name: String, val score: Int) {
        companion object : Projection<HStack2<Int,String>, Score> {
            override fun fromParts(parts: HStack2<Int,String>) = Score(parts.component1(), parts.component2())
            override fun toParts(mapped: Score): HStack2<Int, String> = Empty + mapped.name + mapped.score
        }
    }

    val scores = string + int asA Score

    @Test
    fun route_for_abstracted_pair() {
        assertThat(scores.parse("/bob/30"), equalTo(Score("bob", 30)))
    }

    @Test
    fun reverse_routing_for_abstracted_pair() {
        assertThat(scores.path(Score("alice", 100)), equalTo("/alice/100"))
    }
    
    data class TimestampedScore(val timestamp: Date, val score: Score) {
        companion object : Projection<HStack2<Score,Date>, TimestampedScore> {
            override fun fromParts(parts: HStack2<Score,Date>) = TimestampedScore(parts.component1(), parts.component2())
            override fun toParts(mapped: TimestampedScore) = Empty + mapped.timestamp + mapped.score
        }
    }
    
    val timestampedScores = (root + "at" + timestamps + "score" + scores) asA TimestampedScore

    @Test
    fun route_for_composed_abstractions() {
        assertThat(timestampedScores.parse("/at/1000/score/alice/20"), equalTo(
            TimestampedScore(Date(1000), Score("alice", 20))))
    }

    @Test
    fun reverse_routing_for_composed_abstractions() {
        assertThat(timestampedScores.path(TimestampedScore(Date(3000), Score("bob", 1))),
            equalTo("/at/3000/score/bob/1"))
    }
}
