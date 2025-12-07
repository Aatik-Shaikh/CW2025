/*
 *
 * This class acts as a data carrier object (DTO) for game events.
 * It encapsulates the details of a specific action occurring in the game,
 * such as a brick moving down or rotating.
 *
 * Purpose:
 * By bundling the event type and the event source together, this class allows
 * the game logic to distinguish between:
 * 1. User-initiated actions (pressing a key).
 * 2. System-initiated actions (gravity/game loop).
 *
 * This distinction is crucial for scoring, as points are often awarded for
 * manual drops but not for automatic falling.
 */
package com.comp2042.events;

public final class MoveEvent {

    // The specific type of movement (e.g., DOWN, LEFT, ROTATE)
    private final EventType eventType;

    // The origin of the event (USER or THREAD)
    private final EventSource eventSource;

    /**
     * Constructs a new MoveEvent.
     * * @param eventType   The type of action being performed.
     * @param eventSource The source that triggered this action.
     */
    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventSource getEventSource() {
        return eventSource;
    }
}