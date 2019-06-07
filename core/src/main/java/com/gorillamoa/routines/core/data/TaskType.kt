package com.gorillamoa.routines.core.data

/**
 * Our scheduling system needs to interpret various types of tasks so that
 * it can prioritize effectively.
 */
enum class TaskType{

    /**
     * These task encompass a goal towards which the user is trying to achieve.
     * When it comes to goals, the user usually has a deadline or an expectation to reach.
     * They are trying to change something and so they will devote time to achieve their goals
     * A goal will eventually be accomplished, so the system needs to calculate:
     *
     * - the amount of time (active) it has taken to reach the goal
     * - how many goals were achieved
     * - how to schedule tasks towards this goal as a deadline approaches
     *
     */
    TYPE_GOAL,


    /**
     * Unlike a goal, a habit (in our context) doesn't really have a deadline. Its just a habit
     * that the user is trying to maintain for an unspecified amount of time. We can assume no
     * deadline, or expectation.
     *
     * - Our system should try to incorporate a habit in a frequency specified by the user
     *
     */
    TYPE_HABIT,


    /**
     * The user didn't specify a type, which means that the task is likely a one-time
     * task. Or not really that important. There is no deadline or expectation formed from the user
     * so we don't really need to worry about rescheduling these tasks.
     * We should, however, encourage the user to upgrade this task to a habit or goal, in case
     * they wrote it down in a hurry.. If they do nothing, it was probably negligible.
     * (not that important to them)
     */
    TYPE_UNKNOWN
}