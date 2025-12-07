
package com.comp2042.events;

/**
 * A Data Transfer Object (DTO) representing a movement request within the game.
 * It encapsulates the type of movement (e.g., LEFT, ROTATE) and the source of the event (USER or THREAD).
 */
public final class MoveEvent {

    // The specific type of movement (e.g., DOWN, LEFT, ROTATE)
    private final EventType eventType;

    // The origin of the event (USER or THREAD)
    private final EventSource eventSource;

    /**
     * Constructs a new MoveEvent.
     *
     * @param eventType   The type of action being performed (e.g., DOWN, ROTATE).
     * @param eventSource The source that triggered this action (USER or THREAD).
     */
    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }


    /**
     * Retrieves the type of movement associated with this event.
     *
     * @return The EventType enum value (e.g., LEFT, RIGHT, HARD_DROP).
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Retrieves the source that triggered this event.
     *
     * @return The EventSource enum value (USER or THREAD).
     */
    public EventSource getEventSource() {
        return eventSource;
    }
}