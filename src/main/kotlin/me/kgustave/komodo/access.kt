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
@file:JvmName("FileWatchersKt")
package me.kgustave.komodo

import com.sun.nio.file.ExtendedWatchEventModifier
import me.kgustave.komodo.internal.FileWatcherImpl
import me.kgustave.komodo.internal.fileSystemWatchService
import java.io.File
import java.nio.file.StandardWatchEventKinds.*

/**
 * Creates a new [FileWatcher] for the specified [file].
 *
 * The [watchTree] option may be used to disable a directory file-tree from being watched, otherwise it
 * will watch a directory file-tree when [file] represents a directory.
 *
 * Additionally, the types of [FileEvents][FileEvent] the new FileWatcher receives may be configured using
 * the [config block][block] closure. See [FileWatcher.Config] for more details on the configuration.
 *
 * @param file The file to watch.
 * @param watchTree Whether the FileWatcher should watch the entire file-tree represented by the [file].
 *  By default, this is `true` if the [file is a directory][File.isDirectory], otherwise `false`.
 * @param block The optional configuration for the [FileWatcher], which can be used to filter which
 *  [event types][FileEvent.Type] are received by it.
 *
 * @return A new [FileWatcher].
 *
 * @see FileWatcher
 * @see FileWatcher.Config
 */
fun FileWatcher(file: File, watchTree: Boolean = file.isDirectory, block: (FileWatcher.Config.() -> Unit)? = null): FileWatcher {
    val config = FileWatcher.Config()
    block?.let { config.apply(it) }
    return createFileWatcher(file, watchTree, config)
}

/**
 * Creates a new [FileWatcher] for the receiver [File].
 *
 * The [watchTree] option may be used to disable a directory file-tree from being watched, otherwise it
 * will watch a directory file-tree when the receiver File represents a directory.
 *
 * Additionally, the types of [FileEvents][FileEvent] the new FileWatcher receives may be configured using
 * the [config block][block] closure. See [FileWatcher.Config] for more details on the configuration.
 *
 * @receiver The file to watch.
 * @param watchTree Whether the FileWatcher should watch the entire file-tree represented by the receiver
 *  File. By default, this is `true` if the [file is a directory][File.isDirectory], otherwise `false`.
 * @param block The optional configuration for the [FileWatcher], which can be used to filter which
 *  [event types][FileEvent.Type] are received by it.
 *
 * @return A new [FileWatcher].
 *
 * @see FileWatcher
 * @see FileWatcher.Config
 */
@JvmName("extension_watcher")
fun File.watcher(watchTree: Boolean = isDirectory, block: (FileWatcher.Config.() -> Unit)? = null): FileWatcher =
    FileWatcher(this, watchTree, block)

@JvmName("internal_createFileWatcher")
internal fun createFileWatcher(file: File, watchTree: Boolean, config: FileWatcher.Config): FileWatcher {
    val eventKinds = listOfNotNull(
        ENTRY_CREATE.takeIf { config.watchCreations },
        ENTRY_MODIFY.takeIf { config.watchModifications },
        ENTRY_DELETE.takeIf { config.watchDeletions }
    )

    require(eventKinds.isNotEmpty()) { "FileWatcher must watch some form of file-system event!" }

    val mods = if(!watchTree) emptyArray() else {
        require(file.isDirectory) { "watchTree may not be true if file is not a directory!" }
        arrayOf(ExtendedWatchEventModifier.FILE_TREE)
    }

    val key = file.toPath().register(fileSystemWatchService, eventKinds.toTypedArray(), *mods)
    return FileWatcherImpl(key)
}
