# yetibot.github.io

[![Build Status](https://travis-ci.org/yetibot/yetibot.github.io.svg?branch=source)](https://travis-ci.org/yetibot/yetibot.github.io)

This is the static site generator source for
[https://yetibot.com](https://yetibot.com).

It's built on [Cryogen](http://cryogenweb.org/).

## Prerequisites

- [Leiningen](https://github.com/technomancy/leiningen)
- [Sass](https://sass-lang.com/install)

## Dev usage

Run a Ring server for local dev:

```bash
PORT=4040 lein ring server
```

If working on Javascript, edit `src/index.js` and run:

```bash
yarn dev:watch
```

This will compile the JS with Webpack. Currently does not transpile.

TODO setup a git precommit hook to compile the JS for production so we don't
have to run it in CI for now.

## Build

Generate the static site:

```bash
lein run
```

