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
@file:JvmName("FileWatchers")

package me.kgustave.komodo

import java.io.File

private val DEFAULT_CONFIG_INSTANCE = FileWatcher.Config()

@Deprecated(
    message = "Unnecessary package level function, will be removed in later version",
    replaceWith = ReplaceWith(
        expression = "FileWatcher()",
        imports = ["me.kgustave.komodo.FileWatcher"]
    ),
    level = DeprecationLevel.ERROR
)
fun create(file: File): FileWatcher {
    return createFileWatcher(file, file.isDirectory, DEFAULT_CONFIG_INSTANCE)
}

@Deprecated(
    message = "Unnecessary package level function, will be removed in later version",
    replaceWith = ReplaceWith(
        expression = "FileWatcher()",
        imports = ["me.kgustave.komodo.FileWatcher"]
    ),
    level = DeprecationLevel.ERROR
)
fun create(file: File, watchTree: Boolean): FileWatcher {
    return createFileWatcher(file, watchTree, DEFAULT_CONFIG_INSTANCE)
}

@Deprecated(
    message = "Unnecessary package level function, will be removed in later version",
    replaceWith = ReplaceWith(
        expression = "FileWatcher()",
        imports = ["me.kgustave.komodo.FileWatcher"]
    ),
    level = DeprecationLevel.ERROR
)
fun create(file: File, config: FileWatcher.Config): FileWatcher {
    return createFileWatcher(file, file.isDirectory, config)
}

@Deprecated(
    message = "Unnecessary package level function, will be removed in later version",
    replaceWith = ReplaceWith(
        expression = "FileWatcher()",
        imports = ["me.kgustave.komodo.FileWatcher"]
    ),
    level = DeprecationLevel.ERROR
)
fun create(file: File, watchTree: Boolean, config: FileWatcher.Config): FileWatcher {
    return createFileWatcher(file, watchTree, config)
}

