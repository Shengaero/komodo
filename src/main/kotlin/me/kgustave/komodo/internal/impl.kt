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
package me.kgustave.komodo.internal

import me.kgustave.komodo.FileEvent
import me.kgustave.komodo.FileWatcher
import java.io.File
import java.nio.file.*
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

internal val fileSystemWatchService by lazy { FileSystems.getDefault().newWatchService() }

@Suppress("UNCHECKED_CAST")
internal class FileWatcherImpl(private val key: WatchKey): FileWatcher, AutoCloseable, Sequence<FileEvent> {
    private val queue = ConcurrentLinkedQueue<WatchEvent<Path>>()
    override val isOpen: Boolean get() = key.isValid

    override fun poll(): FileEvent? {
        check(key.isValid) { "FileWatcher has been closed!" }

        // first check queue
        queue.poll()?.let { return it.toEventImpl() }
        val events = key.pollEvents()
        when(events.size) {
            // fast-path: if this event list is empty or only has one event,
            //do not load the queue. This downsizes the overhead a lot for
            //repetitive invocations of poll.
            0 -> return null
            1 -> {
                val event = events[0]
                verifyContextIsPath(event)
                return (event as WatchEvent<Path>).toEventImpl()
            }

            // slow-path, load the queue and then poll the first event and return that.
            else -> {
                for(event in events) {
                    verifyContextIsPath(event)
                    this.queue += event as WatchEvent<Path>
                }
                return queue.poll()?.toEventImpl()
            }
        }
    }

    override fun queue(): List<FileEvent> {
        val queue = LinkedList<FileEvent>()
        while(true) queue += poll() ?: break
        return queue
    }

    override fun flush() {
        queue.clear()
        key.pollEvents()
    }

    override fun close() {
        if(key.isValid) {
            flush()
            key.cancel()
        }
    }

    override fun iterator(): Iterator<FileEvent> = Iter()

    private inner class Iter: Iterator<FileEvent> {
        var next: FileEvent? = null

        override fun hasNext(): Boolean {
            if(!isOpen) return false
            this.next = poll()
            return this.next != null
        }

        override fun next(): FileEvent = this.next ?: throw NoSuchElementException("No next element!")
    }
}

internal data class FileEventImpl(
    override val file: File,
    override val type: FileEvent.Type,
    override val repeated: Boolean
): FileEvent

private fun verifyContextIsPath(event: WatchEvent<*>) {
    val context = event.context()
    check(context is Path) { "Invalid event context: $context" }
}

private fun WatchEvent<Path>.toEventImpl(): FileEventImpl {
    return FileEventImpl(
        file = context().toFile(),
        type = when(kind()) {
            StandardWatchEventKinds.ENTRY_CREATE -> FileEvent.Type.CREATE
            StandardWatchEventKinds.ENTRY_DELETE -> FileEvent.Type.DELETE
            StandardWatchEventKinds.ENTRY_MODIFY -> FileEvent.Type.MODIFY

            else -> error("Encountered an unexpected entry kind!")
        },
        repeated = count() > 1
    )
}
