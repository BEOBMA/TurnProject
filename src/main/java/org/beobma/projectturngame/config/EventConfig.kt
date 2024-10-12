package org.beobma.projectturngame.config

import org.beobma.projectturngame.config.gameevent.BackgammonEvent
import org.beobma.projectturngame.config.gameevent.ChaosEvent
import org.beobma.projectturngame.config.gameevent.DrawGameEvent
import org.beobma.projectturngame.gameevent.Event
import org.beobma.projectturngame.gameevent.EventOption


class EventConfig {
    companion object {
        val eventList: MutableSet<Event> = mutableSetOf()
        val eventOptionList: MutableSet<EventOption> = mutableSetOf()
    }

    init {
        eventList.clear()
        eventOptionList.clear()

        eventConfig()
    }

    private fun eventConfig() {
        ChaosEvent()
        BackgammonEvent()
        DrawGameEvent()
    }
}