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
@file:Suppress("DuplicatedCode")
package me.kgustave.komodo.coroutines

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.selects.whileSelect
import kotlinx.coroutines.supervisorScope
import java.io.File
import kotlin.coroutines.CoroutineContext

/**
 * Awaits a [File's][File] creation.
 */
@ExperimentalCoroutinesApi
suspend fun File.awaitCreation(context: CoroutineContext? = null) {
    // fast-path: file already exists, return immediately
    if(this.exists()) return

    // slow-path: file does not exist, wait for it to be created
    supervisorScope { // use supervisorScope as receive channel's functionality may be different in future versions of kotlinx.coroutines
        val channel = watch(this@awaitCreation, context ?: coroutineContext, watchTree = false) {
            watchCreations = true
            watchDeletions = false
            watchModifications = false
        }

        // while we receive file events that do not match the top receiver, continue to wait
        whileSelect { channel.onReceive { !this@awaitCreation.exists() && it.file != this@awaitCreation } }

        // close channel to end supervisorScope
        channel.cancel()
    }
}

/**
 * Awaits a [File's][File] deletion.
 */
@ExperimentalCoroutinesApi
suspend fun File.awaitDeletion(context: CoroutineContext? = null) {
    // fast-path: file does not exist, return immediately
    if(!this.exists()) return

    // slow-path: file exists, wait for it to be deleted
    supervisorScope { // use supervisorScope as receive channel's functionality may be different in future versions of kotlinx.coroutines
        val channel = watch(this@awaitDeletion, context ?: coroutineContext, watchTree = false) {
            watchDeletions = true
            watchCreations = false
            watchModifications = false
        }

        // while we receive file events that do not match the top receiver, continue to wait
        whileSelect { channel.onReceive { this@awaitDeletion.exists() && it.file != this@awaitDeletion } }

        // close channel to end supervisorScope
        channel.cancel()
    }
}
