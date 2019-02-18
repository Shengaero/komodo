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
@file:Suppress("BlockingMethodInNonBlockingContext")

package me.kgustave.komodo.coroutines.tests

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import me.kgustave.komodo.ExperimentalFileWatcherApi
import me.kgustave.komodo.FileEvent
import me.kgustave.komodo.FileWatcher
import me.kgustave.komodo.coroutines.awaitCreation
import me.kgustave.komodo.coroutines.awaitDeletion
import me.kgustave.komodo.coroutines.watch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
@ExperimentalFileWatcherApi
class CoroutineFileWatcherTests {
    private lateinit var temp: File

    @BeforeEach fun `create temp dir`() {
        temp = createTempDir()
        check(temp.exists()) { "Temp directory was not created!" }
    }

    @Test fun `test watch channel receives file creation`() {
        withChannel({ watchCreations = true }) { channel ->
            createFile()
            delay(500)
            assertEquals(
                expected = FileEvent.Type.CREATE,
                actual = channel.receive().type
            )
        }
    }

    @Test fun `test watch channel receives file modification`() {
        val file = createFile()
        withChannel({ watchModifications = true }) { channel ->
            file.writeText("Hello!")
            delay(500)
            assertEquals(
                expected = FileEvent.Type.MODIFY,
                actual = channel.receive().type
            )
        }
    }

    @Test fun `test watch channel receives file deletion`() {
        val file = createFile()
        withChannel({ watchDeletions = true }) { channel ->
            file.delete()
            delay(500)
            assertEquals(
                expected = FileEvent.Type.DELETE,
                actual = channel.receive().type
            )
        }
    }

    @Test fun `test await file creation`() {
        runBlocking {
            val file = File(temp, "child")
            file.deleteOnExit()
            coroutineScope {
                launch { withTimeout(5000) { file.awaitCreation() } }
                check(file.createNewFile()) { "Failed to create file!" }
            }
        }
    }

    @Test fun `test await file deletion`() {
        runBlocking {
            val file = createFile()
            file.deleteOnExit()
            coroutineScope {
                launch { withTimeout(5000) { file.awaitDeletion() } }
                check(file.delete()) { "Failed to delete file!" }
            }
        }
    }

    @AfterEach fun `destroy temp dir`() {
        check(temp.deleteRecursively())
    }

    private inline fun withChannel(
        crossinline config: FileWatcher.Config.() -> Unit,
        crossinline block: suspend CoroutineScope.(channel: ReceiveChannel<FileEvent>) -> Unit
    ) = runBlocking {
        val channel = watch(temp, Dispatchers.IO) {
            watchCreations = false
            watchDeletions = false
            watchModifications = false
            config()
        }

        try { block(channel) } finally { channel.cancel() }
    }

    private fun createFile(parent: File = temp): File =
        createTempFile(directory = parent)
}
