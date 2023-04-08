# pmatiello.openai-api

A Clojure library for interacting with the OpenAI API.

**Notice:** This is not an official OpenAI project nor is it affiliated with
OpenAI in any way.

## Development

### Running tests

The following command will execute the unit tests:

```
% clj -X:test
```

The following commands will execute the integration tests:

```
% export OPENAI_API_KEY="<OpenAI API key>"
% clj -X:test :patterns '["pmatiello.openai-api.integration-tests"]'
```
