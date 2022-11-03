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

import java.io.File

/**
 * Represents a file-system event received by a [FileWatcher].
 *
 * These have three main types:
 * 1) [Creation][FileEvent.Type.CREATE] - Represents the creation of a new file,
 *    or moving of a pre-existing file to a new location.
 * 2) [Modification][FileEvent.Type.MODIFY] - Represents the modification of a
 *    file. This can typically be the precursor to other types of events as well.
 * 3) [Deletion][FileEvent.Type.DELETE] - Represents the deletion of a file.
 *
 * The types received can be filtered via a [FileWatcher Config][FileWatcher.Config]
 * when creating a new [FileWatcher].
 */
interface FileEvent {
    /**
     * The [File] associated with the [FileEvent].
     */
    val file: File

    /**
     * The [FileEvent.Type] of this [FileEvent].
     */
    val type: Type

    /**
     * Whether this [FileEvent] is a repeated event.
     *
     * This typically occurs when dealing with specific
     * events involving modification of a file.
     */
    val repeated: Boolean

    /**
     * Enum constants for the [type][FileEvent.type]
     * of [FileEvent] received by a [FileWatcher].
     *
     * This can be either [CREATE], [DELETE], or [MODIFY].
     * See [FileEvent] documentation for details.
     *
     * @see FileEvent
     */
    enum class Type { CREATE, DELETE, MODIFY }
}
