# rocksdbjni-miss-perfs

Demonstrate the performances of miss in rocksdbjni

## Run using datadog profiler

Link to [Datadog documentation](https://docs.datadoghq.com/getting_started/profiler/).

Usage:

```bash
./run.sh --api-key DD_API_KEY --site DD_SITE
```

So for instance with an API Key `00000000000000000000000000000000` and site `datadoghq.com`:

```bash
./run.sh --api-key 00000000000000000000000000000000 --site datadoghq.com
```

The script will generate a file `docker.env` with this content:

```bash
DD_API_KEY=00000000000000000000000000000000
DD_SITE=datadoghq.com
```

and use it to pass the Datadog credentials to `docker-compose`.
This will generate profiles and upload them to Datadog.

## Hits and misses keys

To try to compare hit and misses, the keys are interleaved, the hits are done with 1M even keys with the format [%08d](https://github.com/trazfr/rocksdbjni-miss-perfs/blob/main/src/main/java/test/App.java#L14):

- 00000000
- 00000002
- 00000004...
- 01999998

The misses are odd keys:

- 00000001
- 00000003...
- 01999999
