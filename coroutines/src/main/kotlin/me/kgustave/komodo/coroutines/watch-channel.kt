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
@file:JvmName("WatchChannel")
package me.kgustave.komodo.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import me.kgustave.komodo.FileEvent
import me.kgustave.komodo.FileWatcher
import java.io.File
import java.time.Duration
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@ExperimentalCoroutinesApi
fun CoroutineScope.watch(
    file: File,
    context: CoroutineContext = EmptyCoroutineContext,
    capacity: Int = Channel.RENDEZVOUS,
    timeout: Duration = Duration.ofMillis(500),
    watchTree: Boolean = file.isDirectory,
    config: (FileWatcher.Config.() -> Unit)? = null
): ReceiveChannel<FileEvent> {
    // checks
    require(file.isDirectory) { "file does not represent a directory!" }
    require(file.exists()) { "file must exist!" }
    require(!timeout.isZero && !timeout.isNegative) { "timeout must be a non-zero positive duration!" }

    // create a file watcher
    val watcher = FileWatcher(file, watchTree, config)

    // return a producer channel
    return produce(context = context, capacity = capacity) {

        // on close, close the watcher as well
        invokeOnClose { watcher.close() }

        while(!isClosedForSend && watcher.isOpen) {
            // iterate and send events
            for(event in watcher.queue()) {
                send(event)
            }

            // timeout
            delay(timeout.toMillis())
        }
    }
}
