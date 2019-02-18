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
package me.kgustave.komodo.tests

import me.kgustave.komodo.ExperimentalFileWatcherApi
import me.kgustave.komodo.FileEvent
import me.kgustave.komodo.FileWatcher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.lang.IllegalArgumentException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExperimentalFileWatcherApi
class FileWatcherTests {
    private lateinit var temp: File

    @BeforeEach fun `create temp dir`() {
        temp = createTempDir()
        check(temp.exists()) { "Temp directory was not created!" }
    }

    @Test fun `test watcher detects file creation`() {
        withWatcher({ watchCreations = true }) { watcher ->
            createFile()
            safeDelay()
            assertEquals(
                expected = FileEvent.Type.CREATE,
                actual = watcher.poll()?.type
            )
        }
    }

    @Test fun `test watcher detects file modification`() {
        val file = createFile()
        withWatcher({ watchModifications = true }) { watcher ->
            file.writeText("Hello!")
            safeDelay()
            assertEquals(
                expected = FileEvent.Type.MODIFY,
                actual = watcher.poll()?.type
            )
        }
    }

    @Test fun `test watcher detects file deletion`() {
        val file = createFile()
        withWatcher({ watchDeletions = true }) { watcher ->
            file.delete()
            safeDelay()
            assertEquals(
                expected = FileEvent.Type.DELETE,
                actual = watcher.poll()?.type
            )
        }
    }

    @Test fun `test watcher fails to be created when no event types are specified`() {
        assertFailsWith<IllegalArgumentException> {
            FileWatcher(temp) {
                watchCreations = false
                watchDeletions = false
                watchModifications = false
            }
        }
    }

    @AfterEach fun `destroy temp dir`() {
        check(temp.deleteRecursively())
    }

    private inline fun withWatcher(crossinline config: FileWatcher.Config.() -> Unit, block: (watcher: FileWatcher) -> Unit) =
        FileWatcher(temp) {
            watchCreations = false
            watchDeletions = false
            watchModifications = false
            config()
        }.use(block)

    private fun safeDelay() = Thread.sleep(500)

    private fun createFile(parent: File = temp): File =
        createTempFile(directory = parent)
}
