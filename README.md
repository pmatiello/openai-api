# me.pmatiello/openai-api

This library provides a pure-Clojure wrapper around the
[OpenAI API](https://platform.openai.com), offering various functions for interacting
with the API's capabilities. These include text generation, image generation and
editing, embeddings, audio transcription and translation, file management,
fine-tuning, and content moderation.

**Notice:** This is not an official OpenAI project nor is it affiliated with
OpenAI in any way.

## Usage

This library is available on the [clojars](https://clojars.org) repository. Refer to
the link in the image below for instructions on how to add it as a dependency to a
Clojure project.

[![Clojars Project](https://img.shields.io/clojars/v/me.pmatiello/openai-api.svg)](https://clojars.org/me.pmatiello/openai-api)

The functions for interacting with the OpenAI API are located in the 
`me.pmatiello.openai-api.api` namespace. An API key is required for usage.

```clj
(require '[me.pmatiello.openai-api.api :as openai])

(def credentials
  (openai/credentials api-key))

(openai/chat {:model    "gpt-3.5-turbo"
              :messages [{:role    "user"
                          :content "Fix: (println \"hello"}]}
             credentials)
```

Refer to the function specs and the official
[OpenAI API reference](https://platform.openai.com/docs/api-reference) for details
about the parameters required for these functions.

## Development

Information for developing this library.

### Running tests

The following command will execute the unit tests:

```
% clj -X:test
```

### Building

The following command will build a jar file:

```
% clj -T:build jar
```

To clean a previous build, run:

```
% clj -T:build clean
```

### Releasing

Before releasing, update the library version in the [build.clj](./build.clj) file.

Make a commit and generate a new tag:

```
% git commit -a -m "Release: ${VERSION}"
% git tag -a "v${VERSION}" -m "Release: ${VERSION}"
% git push
% git push origin "v${VERSION}" 
```

To release to [clojars](https://clojars.org), run:

```
% mvn deploy:deploy-file \
      -Dfile=target/openai-api-${VERSION}.jar \
      -DrepositoryId=clojars \
      -Durl=https://clojars.org/repo \
      -DpomFile=target/classes/META-INF/maven/me.pmatiello/openai-api/pom.xml
```

Notice that this step requires clojars to be configured as a server in the local
`~/.m2/settings.xml` file.

## Contribution Policy

This software is open-source, but closed to contributions.
