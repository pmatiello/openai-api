# pmatiello.openai-api

This library provides a wrapper around the [OpenAI API](https://platform.openai.com),
offering various functions for interacting with the API's capabilities. These include
text generation, image generation and editing, embeddings, audio transcription and 
translation, file management, fine-tuning, and content moderation.

**Notice:** This is not an official OpenAI project nor is it affiliated with
OpenAI in any way.

## Usage

The functions for interacting with the OpenAI API are located in the 
`pmatiello.openai-api.api` namespace. An API key is required for usage.

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

## Contribution Policy

This software is open-source, but closed to contributions.
