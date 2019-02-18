[license]: https://img.shields.io/badge/License-Apache-red.svg?style=flat-square

[ ![license][] ](https://github.com/Shengaero/komodo/blob/master/LICENSE)

# Komodo

A simple JVM library for watching files.

## Usage and Examples

Komodo makes it easy to watch files on the JVM in Java or Kotlin!

```kotlin
private val baseDir = File(System.getProperty("user.dir"))

// A simple kotlin program using Komodo.
// Run in a directory to watch the directory for the next file-system event.
fun main(args: Array<String>) {
    val watcher = FileWatcher(baseDir) {
        // enabled which events to watch for using the configuration closure!
        watchCreations = true
        watchModifications = true
        watchDeletions = true
    }

    while(true) {
        val event = watcher.poll() ?: continue
        println("File event received! Type: ${event.type}. Path: '${event.file}'.")
        break
    }

    watcher.close()

    println("End program...")
}
```

Komodo is written in Kotlin, but is Java friendly, and can be used from normal Java code with ease!

```java
// A simple java program using Komodo.
// Run in a directory to watch the directory for the next file-system event.
public class Main {
    private static final File BASE_DIR = new File(System.getProperty("user.dir"));
    
    public static void main(String[] args)
    {
        FileWatcher.Config config = new FileWatcher.Config();

        config.setWatchCreations(true);
        config.setWatchModifications(true);
        config.setWatchDeletions(true);

        FileWatcher watcher = FileWatchers.create(BASE_DIR, config);

        while(true)
        {
            FileEvent event = watcher.poll();
            if(event == null) continue;
            System.out.println("File event received! Type: " + event.getType().toString() + ". Path: '" + event.getFile().toString() + "'.");
            break;
        }

        watcher.close();

        System.out.println("End program...");
    }
}
```

There is also a module that has [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines) integration!

```kotlin
private val baseDir = File(System.getProperty("user.dir"))

// A simple kotlin program using Komodo with coroutines.
// Run in a directory to watch the directory for the next file-system event.
fun main(args: Array<String>) = runBlocking {
    val watcher = watch(baseDir) {
        watchCreations = true
        watchModifications = true
        watchDeletions = true
    }

    val event = watcher.receive()

    println("File event received! Type: ${event.type}. Path: '${event.file}'.")

    watcher.close()

    println("End program...")
}
```

## License

Komodo is licensed under the [Apache 2.0 License](https://github.com/Shengaero/komodo/blob/master/LICENSE)

```
   Copyright 2018 Kaidan Gustave
   
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
   
       http://www.apache.org/licenses/LICENSE-2.0
   
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
