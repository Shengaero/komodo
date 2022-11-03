/*
 * Copyright 2019 Kaidan Gustave
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.kgustave.komodo

/**
 * File watcher which listens and provides [FileEvents][FileEvent] when certain actions are provided in
 * specific areas of the file-system.
 *
 * This can be configured to monitor a single file or an entire file tree:
 *
 * ```kotlin
 * val file = File("file.txt")
 *
 * val watcher = FileWatcher(file) {
 *     watchCreations = false
 *     watchModifications = true
 *     watchDeletions = true
 * }
 *
 * val event = file.poll()
 *
 * // ...
 * ```
 *
 * This interface also implements the [Sequence] abstract type, allowing it to be sequentially iterated.
 *
 * Operations involving iteration drain the queue for all available events, and should not be used in cases
 * involving multithreaded access of instances of this type.
 *
 * With that also keep in mind this implementation is merely a queue-like wrapper for the [java.nio.file.WatchService]
 * API. The standard implementation does not implement any multithreading or concurrency beyond using a queue to store
 * polled events, and should be externally synchronized if thread safety is required.
 *
 * @see me.kgustave.komodo.FileEvent
 */
interface FileWatcher: AutoCloseable, Sequence<FileEvent> {
    /**
     * Whether or not this FileWatcher is open.
     *
     * When `false` calls to [poll] and [queue] will result
     * in an [IllegalStateException] being thrown.
     */
    val isOpen: Boolean

    /**
     * Polls the next available [FileEvent] from this FileWatcher,
     * returning it if present, otherwise returning `null`.
     *
     * If [isOpen] is `false`, this function will throw an
     * [IllegalStateException] when called.
     *
     * @return The next available FileEvent or `null`.
     */
    fun poll(): FileEvent?

    /**
     * Returns the entire remaining queue of [FileEvents][FileEvent].
     *
     * If [isOpen] is `false`, this function will throw an
     * [IllegalStateException] when called.
     *
     * @return The entire remaining queue of FileEvents.
     */
    fun queue(): List<FileEvent>

    /**
     * Flushes the remaining queue of [FileEvents][FileEvent].
     *
     * This operation does nothing if the queue is empty, or
     * this FileWatcher is closed.
     */
    fun flush()

    /**
     * Configurations for a new [FileWatcher] instance.
     *
     * This can be used to filter out the [types of events][FileEvent.Type]
     * received by a FileWatcher.
     *
     * By default, all types of events are received.
     */
    class Config {
        /**
         * Whether the [FileWatcher] should watch for [FileEvents][FileEvent]
         * with the [CREATE][FileEvent.Type.CREATE] event type.
         */
        var watchCreations = true

        /**
         * Whether the [FileWatcher] should watch for [FileEvents][FileEvent]
         * with the [MODIFY][FileEvent.Type.MODIFY] event type.
         */
        var watchModifications = true

        /**
         * Whether the [FileWatcher] should watch for [FileEvents][FileEvent]
         * with the [DELETE][FileEvent.Type.DELETE] event type.
         */
        var watchDeletions = true
    }
}
