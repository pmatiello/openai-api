# Changelog

Notable changes to this project are documented in this file.

## v0.5.0
**Released at:** UNRELEASED
- Adds API calls for the new fine-tuning API.
- Deprecates calls to edits and fine-tunes APIs, as these APIs have been
  deprecated by OpenAI.
- Removes specs for functions calling the OpenAI API.

## v0.4.1
**Released at:** 2023-06-08.
- Removes internal namespace from cljdoc.

## v0.4.0
**Released at:** 2023-06-08.
- Adds support for streaming responses in completion and chat functions.
- Adds examples to function docstrings.

## v0.3.1
**Released at:** 2023-05-19.
- Fixes changelog formatting.

## v0.3.0
**Released at:** 2023-05-15.
- Adds support for specifying connection and socket timeouts.

## v0.2.0
**Released at:** 2023-05-09.
- Adds support for custom base-url for accessing the API.
- Breaking change: replaces `api/credentials` with `api/config`.

## v0.1.0 
**Released at:** 2023-04-25.
- Adds API calls for text generation, image generation and editing, embeddings, 
audio transcription and translation, file management, fine-tuning, and content 
moderation.
