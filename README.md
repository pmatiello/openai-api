# me.pmatiello/openai-api

This library provides a pure-Clojure wrapper around the
[OpenAI API](https://platform.openai.com), offering various functions for interacting
with the API's capabilities. These include text generation, image generation and
editing, embeddings, audio generation, transcription and translation, file management,
fine-tuning, and content moderation.

**Notice:** This is not an official OpenAI project nor is it affiliated with
OpenAI in any way.

## Usage

This library is available on the [clojars](https://clojars.org) repository. Refer to
the link in the Clojars badge below for instructions on how to add it as a dependency 
to a Clojure project.

[![Clojars Project](https://img.shields.io/clojars/v/me.pmatiello/openai-api.svg)](https://clojars.org/me.pmatiello/openai-api)
[![cljdoc badge](https://cljdoc.org/badge/me.pmatiello/openai-api)](https://cljdoc.org/d/me.pmatiello/openai-api)

The functions for interacting with the OpenAI API are located in the 
`me.pmatiello.openai-api.api` namespace. Documentation for these functions is available
in their docstrings.

Refer to the official
[OpenAI API reference](https://platform.openai.com/docs/api-reference) for details
about the parameters required for these functions and for their output format.

Calls to the OpenAI API require a `config` parameter, which can be produced 
using the `me.pmatiello.openai-api.api/config` function. A valid OpenAI API key
is required as an argument.

## Tutorial

As mentioned above, the public interface for this library is present at the
`me.pmatiello.openai-api.api` namespace.

```clj
(require '[me.pmatiello.openai-api.api :as openai])
```

The following code produces the configuration value required by all API calls. A valid
API key must be passed as the value after the `:api-key` keyword.

```clj
(def config
  (openai/config :api-key api-key))
```

Once the `config` value is ready, it is possible to execute API calls. For instance, 
the call below retrieves the list of all available models.

```clj
(openai/models config)
```

And the next one retrieves the details of a specific model.

```clj
(openai/model "gpt-3.5-turbo" config)
```

The following call requests a chat completion. In this particular case, the model is
prompted to produce a "hello world" program written in Clojure.

```clj
(openai/chat
  {:model    "gpt-3.5-turbo"
   :messages [{:role "user" :content "Hello!"}
              {:role "assistant" :content "Hello! How can I assist you today?"}
              {:role "user" :content "Write a hello world program in Clojure."}]}
  config)
```

More examples are available in the [test/repl.clj](test/repl.clj) file.

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

Before releasing, update the library version in the [build.clj](./build.clj) file and
include a entry for the release in the [changelog](./CHANGELOG.md).

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
